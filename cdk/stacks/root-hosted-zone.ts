import { Construct } from 'constructs';
import { Stack, StackProps } from 'aws-cdk-lib';
import { IPublicHostedZone, PublicHostedZone } from 'aws-cdk-lib/aws-route53';

export class RootHostedZone extends Stack {
  public readonly hostedZone: IPublicHostedZone;

  constructor(scope: Construct, id: string, props: StackProps) {
    super(scope, id, props);
    this.hostedZone = PublicHostedZone.fromPublicHostedZoneAttributes(this, 'daniel-eichman.com', {
      hostedZoneId: 'Z31YF47RUSVUFM',
      zoneName: 'daniel-eichman.com',
    });
  }
}
