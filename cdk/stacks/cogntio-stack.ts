import { Construct } from 'constructs';
import { Stack, StackProps } from 'aws-cdk-lib';
import { PROJECT } from '../bin/config';
import {
  AccountRecovery,
  Mfa,
  UserPool,
  UserPoolClient,
  UserPoolClientIdentityProvider,
  UserPoolEmail,
} from 'aws-cdk-lib/aws-cognito';
import { StandardAttribute } from 'aws-cdk-lib/aws-cognito/lib/user-pool-attr';

export interface CognitoStackProps extends StackProps {
  callbackURL: string;
  supportEmail: string;
}
export class CognitoStack extends Stack {
  readonly userPool: UserPool;
  constructor(scope: Construct, id: string, props: CognitoStackProps) {
    super(scope, id, props);
    const required: StandardAttribute = {
      mutable: true,
      required: true,
    };
    this.userPool = new UserPool(this, `${PROJECT}-UserPool`, {
      selfSignUpEnabled: true,
      userPoolName: `${PROJECT}-UserPool`,
      userVerification: {
        emailSubject: `${PROJECT} Account Registration`,
        emailBody: `Thank you for signing up for ${PROJECT}. Your verification code is {####}.`,
      },
      enableSmsRole: false,
      signInAliases: { email: true },
      autoVerify: { email: true },
      standardAttributes: {
        email: required,
        givenName: required,
        familyName: required,
      },
      mfa: Mfa.OFF,
      accountRecovery: AccountRecovery.EMAIL_ONLY,
      email: UserPoolEmail.withCognito(props.supportEmail),
      deletionProtection: true,
    });

    new UserPoolClient(this, `${PROJECT}-UserPoolClient`, {
      userPool: this.userPool,
      supportedIdentityProviders: [UserPoolClientIdentityProvider.COGNITO],
      authFlows: {
        userPassword: true,
      },
      oAuth: {
        callbackUrls: [props.callbackURL],
      },
    });

    this.userPool.addDomain(`${PROJECT}-UserPoolDomain`, {
      cognitoDomain: {
        //Hosted UI
        domainPrefix: `${PROJECT.toLowerCase()}-oauth-service`,
      },
    });
  }
}
