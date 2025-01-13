#!/usr/bin/env node
import 'source-map-support/register';
import { App, StackProps } from 'aws-cdk-lib';
import { PROJECT, stages } from './config';
import { RootHostedZone } from '../stacks/root-hosted-zone';
import { WebsiteStack } from '../stacks/website-stack';
import { ServiceStack } from '../stacks/service-stack';
import { CognitoStack } from '../stacks/cogntio-stack';
import { MonitoringStack } from '../stacks/monitoring';

const app = new App();

stages.forEach((stage) => {
  const stackProps: StackProps = {
    env: { account: stage.account, region: stage.region },
  };

  const rootHostedZone = new RootHostedZone(app, `${PROJECT}-DNS-Stack-${stage.name}`, {
    ...stackProps,
  });

  const cognitoStack = new CognitoStack(app, `${PROJECT}-Cognito-Stack-${stage.name}`, {
    callbackURL: `https://${rootHostedZone.hostedZone.zoneName}`,
    supportEmail: `no-reply@${rootHostedZone.hostedZone.zoneName}`,
    ...stackProps,
  });

  new WebsiteStack(app, `${PROJECT}-Website-Stack-${stage.name}`, {
    hostedZone: rootHostedZone.hostedZone,
    domainName: rootHostedZone.hostedZone.zoneName,
    ...stackProps,
  });

  const serviceStack = new ServiceStack(app, `${PROJECT}-Service-Stack-${stage.name}`, {
    apiDomainName: `api.${rootHostedZone.hostedZone.zoneName}`,
    hostedZone: rootHostedZone.hostedZone,
    stageName: stage.name,
    userPoolArn: cognitoStack.userPool.userPoolArn,
    cognitoEnv: cognitoStack.cognitoEnv,
    ...stackProps,
  });

  new MonitoringStack(app, `${stage.name}-${PROJECT}-Monitoring-Stack`, {
    apiName: serviceStack.apiName,
    dashboardName: `Monitoring-${stage.name}`,
    operations: serviceStack.operations,
    region: stage.region,
    stage: stage.name,
    ddbTables: [],
  });
});
