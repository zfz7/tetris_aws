import { InfoOutput, SayHelloInput, SayHelloOutput } from "ts-client";

const baseUrl = `https://api.${window.location.hostname}`;
export const getHello = (
  input: SayHelloInput,
  bearerToken: string,
): Promise<SayHelloOutput> => {
  return fetch(
    `${baseUrl}/hello?` + new URLSearchParams({ name: input.name! }),
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${bearerToken}`,
      },
    },
  ).then(
    (response) => response.json(),
    (err) => console.log(err),
  );
};

export const getInfo = (): Promise<InfoOutput> => {
  return fetch(`${baseUrl}/info`, {
    method: "GET",
  }).then(
    (response) => response.json(),
    (err) => console.log(err),
  );
};
