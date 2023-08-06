#!/usr/bin/env node
import 'source-map-support/register';
import {App, StackProps} from "aws-cdk-lib";
import {stages} from "./config";
import {RootHostedZone} from "../stacks/root-hosted-zone";
import {WebsiteStack} from "../stacks/website-stack";
import {ServiceStack} from "../stacks/service-stack";

const app = new App();

stages.forEach(stage => {
    const stackProps: StackProps = {
        env: {account: stage.account, region: stage.region}
    }

    const rootHostedZone = new RootHostedZone(app, `DNS-Stack-${stage.name}`, {
        ...stackProps
    });

    new WebsiteStack(app, `Website-Stack-${stage.name}`, {
        hostedZone: rootHostedZone.hostedZone,
        domainName: rootHostedZone.hostedZone.zoneName,
        ...stackProps
    });

    new ServiceStack(app, `Service-Stack-${stage.name}`, {
        apiDomainName: `api.${rootHostedZone.hostedZone.zoneName}`,
        hostedZone: rootHostedZone.hostedZone,
        stageName: stage.name,
        ...stackProps
    });
})
