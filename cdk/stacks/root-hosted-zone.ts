import { Construct } from 'constructs';
import { Stack, StackProps } from 'aws-cdk-lib';
import { IPublicHostedZone, PublicHostedZone } from 'aws-cdk-lib/aws-route53';
import { PROJECT, ROOT_HOSTED_ZONE_ID, ROOT_HOSTED_ZONE_NAME } from '../bin/config';

export class RootHostedZone extends Stack {
  public readonly hostedZone: IPublicHostedZone;

  constructor(scope: Construct, id: string, props: StackProps) {
    super(scope, id, props);
    this.hostedZone = PublicHostedZone.fromPublicHostedZoneAttributes(this, `${PROJECT}-${ROOT_HOSTED_ZONE_NAME}-HZ`, {
      hostedZoneId: ROOT_HOSTED_ZONE_ID,
      zoneName: ROOT_HOSTED_ZONE_NAME,
    });
  }
}
