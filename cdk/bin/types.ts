export interface Stage {
  readonly account: string;
  readonly name: string;
  readonly region: string;
  readonly isProd: boolean;
}

export interface CognitoEnvironmentVariables {
  readonly REGION: string;
  readonly USER_POOL_ID: string;
  readonly USER_POOL_WEB_CLIENT_ID: string;
}

export interface Operation {
  readonly method: string;
  readonly path: string;
  readonly name: string;
}
