import { Construct } from 'constructs';
import { Stack, StackProps } from 'aws-cdk-lib';
import { Bucket, BucketAccessControl } from 'aws-cdk-lib/aws-s3';
import { BucketDeployment, Source } from 'aws-cdk-lib/aws-s3-deployment';
import * as path from 'path';
import { Distribution, OriginAccessIdentity } from 'aws-cdk-lib/aws-cloudfront';
import { S3Origin } from 'aws-cdk-lib/aws-cloudfront-origins';
import { ARecord, IPublicHostedZone, RecordTarget } from 'aws-cdk-lib/aws-route53';
import { DnsValidatedCertificate } from 'aws-cdk-lib/aws-certificatemanager';
import { CloudFrontTarget } from 'aws-cdk-lib/aws-route53-targets';

export interface WebsiteStackProps extends StackProps {
  hostedZone: IPublicHostedZone;
  domainName: string;
}

export class WebsiteStack extends Stack {
  constructor(scope: Construct, id: string, props: WebsiteStackProps) {
    super(scope, id, props);

    const websiteBuck = new Bucket(this, 'Website-Bucket', {
      accessControl: BucketAccessControl.PRIVATE,
    });

    new BucketDeployment(this, 'Bucket-Deployment', {
      destinationBucket: websiteBuck,
      sources: [Source.asset(path.resolve('../frontend/', 'build'))],
    });

    const originAccessIdentity = new OriginAccessIdentity(this, 'OriginAccessIdentity');
    websiteBuck.grantRead(originAccessIdentity);

    //Should use DnsValidatedCertificate per:
    //https://docs.aws.amazon.com/cdk/api/v1/docs/aws-certificatemanager-readme.html#cross-region-certificates
    const certificate = new DnsValidatedCertificate(this, 'Website-Certificate', {
      hostedZone: props.hostedZone,
      domainName: props.domainName,
      region: 'us-east-1',
    });

    const distribution = new Distribution(this, 'Distribution', {
      defaultRootObject: 'index.html',
      defaultBehavior: {
        origin: new S3Origin(websiteBuck, { originAccessIdentity }),
      },
      domainNames: [props.domainName],
      certificate: certificate,
    });

    new ARecord(this, `Website-ARecord`, {
      zone: props.hostedZone,
      recordName: props.domainName,
      target: RecordTarget.fromAlias(new CloudFrontTarget(distribution)),
    });
  }
}
