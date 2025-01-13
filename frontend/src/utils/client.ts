import { InfoOutput, SayHelloInput, SayHelloOutput, Tetris } from "ts-client";
import { Auth } from "aws-amplify";
import { IdentityProvider } from "@smithy/types/dist-types/identity/identity";
import { TokenIdentity } from "@smithy/types/dist-types/identity/tokenIdentity";

const baseUrl = `https://api.${window.location.hostname}`;

const tokenProvider: IdentityProvider<TokenIdentity> = async () => {
  const session = await Auth.currentSession();
  return Promise.resolve({
    token: session.getIdToken()?.getJwtToken()!,
    expiration: new Date(session.getIdToken().getExpiration() * 1000),
  });
};

const client = new Tetris({
  endpoint: baseUrl,
  region: "us-west-2",
  token: tokenProvider,
});

export const getHello = (input: SayHelloInput): Promise<SayHelloOutput> => {
  return client.sayHello(input);
};

export const getInfo = (): Promise<InfoOutput> => {
  return fetch(`${baseUrl}/info`, {
    method: "GET",
  }).then(
    (response) => response.json(),
    (err) => console.log(err),
  );
};
