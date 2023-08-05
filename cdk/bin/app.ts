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

    const hostedZone = new RootHostedZone(app, `DNS-Stack-${stage.name}`, {
        ...stackProps
    });

    new WebsiteStack(app, `Website-Stack-${stage.name}`, {
        hostedZone: hostedZone.hostedZone,
        domainName: hostedZone.hostedZone.zoneName,
        ...stackProps
    });

    new ServiceStack(app, `Service-Stack-${stage.name}`, {
        stageName: stage.name,
        ...stackProps
    });
})
