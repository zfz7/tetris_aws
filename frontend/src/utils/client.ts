import { InfoOutput, SayHelloInput, SayHelloOutput, Tetris } from "ts-client";

const baseUrl = `https://api.${window.location.hostname}`;
export const getHello = (
  input: SayHelloInput,
  bearerToken: string,
): Promise<SayHelloOutput> => {
  const client = new Tetris({
    endpoint: baseUrl,
    region: "us-west-2",
    token: {
      token: bearerToken,
    },
  });

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
