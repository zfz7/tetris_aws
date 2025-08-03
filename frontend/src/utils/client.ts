import { InfoOutput, SayHelloInput, SayHelloOutput, Tetris } from "ts-client";
import { IdentityProvider } from "@smithy/types/dist-types/identity/identity";
import { TokenIdentity } from "@smithy/types/dist-types/identity/tokenIdentity";
import { fetchAuthSession, signOut } from "aws-amplify/auth";

const baseUrl = `https://api.${window.location.hostname}`;

const tokenProvider: IdentityProvider<TokenIdentity> = async () => {
  try {
    const session = await fetchAuthSession();
    return Promise.resolve({
      token: session.tokens?.idToken?.toString()!,
      expiration: new Date(session.tokens!!.idToken!!.payload!!.exp!! * 1000),
    });
  } catch (error) {
    console.error("Error fetching the current session:", error);
    await signOut();
    return Promise.reject(new Error("Failed to fetch session token."));
  }
};

const client = new Tetris({
  endpoint: baseUrl,
  region: "us-west-2",
  token: tokenProvider,
});

export const getHello = (input: SayHelloInput): Promise<SayHelloOutput> => {
  return client.sayHello(input);
};

const noAuthClient = new Tetris({
  endpoint: baseUrl,
  region: "us-west-2",
});

export const getInfo = (): Promise<InfoOutput> => {
  return noAuthClient.info();
};
