import {Stage} from "./types";

export const beta: Stage = {
    isProd: false,
    name: 'Beta',
    region: 'us-west-2',
    account: process.env.AWS_ACCOUNT!
}
export const stages: Stage[] = [beta];
