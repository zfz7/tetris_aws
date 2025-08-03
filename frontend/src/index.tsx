import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import { getInfo } from "./utils/client";
import { Amplify } from "aws-amplify";

const config = await getInfo();

Amplify.configure({
  Auth: {
    Cognito: {
      identityPoolId: "",
      userPoolId: config.userPoolId ?? "",
      userPoolClientId: config.userPoolWebClientId ?? "",
    },
  },
});

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement,
);
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
