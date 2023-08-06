import {Stage} from "./types";

export const PROJECT = 'Tetris'
export const AWS_ACCOUNT = process.env.AWS_ACCOUNT!
export const ROOT_HOSTED_ZONE_ID = 'Z31YF47RUSVUFM'
export const ROOT_HOSTED_ZONE_NAME = 'daniel-eichman.com'
export const beta: Stage = {
    isProd: false,
    name: 'Beta',
    region: 'us-west-2',
    account: AWS_ACCOUNT
}
export const stages: Stage[] = [beta];
