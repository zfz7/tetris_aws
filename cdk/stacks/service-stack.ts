import { Construct } from 'constructs';
import { Stack, StackProps } from 'aws-cdk-lib';
import {
  ApiDefinition,
  BasePathMapping,
  DomainName,
  EndpointType,
  SecurityPolicy,
  SpecRestApi,
} from 'aws-cdk-lib/aws-apigateway';
import { Code, Function, IFunction, Runtime } from 'aws-cdk-lib/aws-lambda';
import { Effect, PolicyDocument, PolicyStatement, Role, ServicePrincipal } from 'aws-cdk-lib/aws-iam';
import { RegionInfo } from 'aws-cdk-lib/region-info';
import * as fs from 'fs';
import { Certificate, CertificateValidation } from 'aws-cdk-lib/aws-certificatemanager';
import { ARecord, IHostedZone, RecordTarget } from 'aws-cdk-lib/aws-route53';
import { ApiGatewayDomain } from 'aws-cdk-lib/aws-route53-targets';

export interface ServiceStackProps extends StackProps {
  stageName: string;
  apiDomainName: string;
  hostedZone: IHostedZone;
}
export class ServiceStack extends Stack {
  constructor(scope: Construct, id: string, props: ServiceStackProps) {
    super(scope, id, props);
    const lambda = new Function(this, 'Backend-Lambda', {
      code: Code.fromAsset('../backend/build/libs/backend-all.jar', { deployTime: true }),
      handler: 'com.backend.LambdaMain',
      runtime: Runtime.JAVA_17,
    });

    lambda.addToRolePolicy(
      new PolicyStatement({
        actions: ['execute-api:Invoke'],
        resources: ['*'],
        effect: Effect.ALLOW,
      }),
    );

    const apiGatewayRole = getApiGatewayRole(this, `Backend-${props.stageName}-ApiExecutionRole`, lambda);

    const api = new SpecRestApi(this, 'Backend-Apigateway', {
      restApiName: 'Backend-Api',
      description: 'Backend-Api',
      apiDefinition: ApiDefinition.fromInline(
        getOpenApiDefinition(lambda.functionArn, props.env!.region!, apiGatewayRole),
      ),
      deploy: true,
      deployOptions: {
        stageName: props.stageName,
      },
      disableExecuteApiEndpoint: true,
    });

    const domainName = new DomainName(this, `Backend-${props.stageName}-ApiGatwayDomain`, {
      domainName: props.apiDomainName,
      endpointType: EndpointType.REGIONAL,
      securityPolicy: SecurityPolicy.TLS_1_2,
      certificate: new Certificate(this, `Backend-${props.stageName}-ApiCertificate`, {
        domainName: props.apiDomainName,
        validation: CertificateValidation.fromDns(props.hostedZone),
      }),
    });

    new BasePathMapping(this, `Backend-${props.stageName}-BasePathMapping`, {
      domainName: domainName,
      restApi: api,
      stage: api.deploymentStage,
    });

    new ARecord(this, `Backend-${props.stageName}-ApiAliasRecord`, {
      recordName: props.apiDomainName,
      target: RecordTarget.fromAlias(new ApiGatewayDomain(domainName)),
      zone: props.hostedZone,
    });
  }
}

function getApiGatewayRole(stack: Stack, roleName: string, lambdaFunction: IFunction) {
  return new Role(stack, roleName, {
    assumedBy: new ServicePrincipal('apigateway.amazonaws.com'),
    inlinePolicies: {
      agwInvokeLambda: new PolicyDocument({
        statements: [
          new PolicyStatement({
            actions: ['lambda:InvokeFunction'],
            effect: Effect.ALLOW,
            resources: [lambdaFunction.functionArn],
          }),
        ],
      }),
    },
    roleName,
  });
}
function getOpenApiDefinition(functionArn: string, region: string, apiGatewayRole: Role): Record<string, unknown> {
  const modelPath = `../model/build/smithyprojections/model/source/openapi/Tetris.openapi.json`;

  if (!fs.existsSync(modelPath)) {
    throw new Error(`Cannot find Open API definition. Path Recipe: ${modelPath}`);
  }

  const awsPartition = RegionInfo.get(region).partition || 'aws';

  let modelFile = fs.readFileSync(modelPath, 'utf8');
  // Find the Smithy @aws.apigateway#integration placeholders and replace them with the correct values.
  modelFile = modelFile.replace(/\${AWS::Partition}/g, awsPartition);
  modelFile = modelFile.replace(/\${AWS::Region}/g, region);
  modelFile = modelFile.replace(/\${LambdaFunction.Arn}/g, functionArn);
  modelFile = modelFile.replace(/\${APIGatewayExecutionRole.Arn}/g, apiGatewayRole.roleArn);
  modelFile = modelFile.replace(/\${DeployVersion}/g, '1.0.0');
  return JSON.parse(modelFile);
}
