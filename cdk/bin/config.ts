import { Stage } from './types';

export const PROJECT = 'Tetris';
export const AWS_ACCOUNT = process.env.AWS_ACCOUNT!;
export const ROOT_HOSTED_ZONE_ID = process.env.ROOT_HOSTED_ZONE_ID!;
export const ROOT_HOSTED_ZONE_NAME = process.env.ROOT_HOSTED_ZONE_NAME!;
export const beta: Stage = {
  isProd: false,
  name: 'Beta',
  region: 'us-west-2',
  account: AWS_ACCOUNT,
};
export const stages: Stage[] = [beta];
