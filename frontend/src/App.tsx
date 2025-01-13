import React, { useEffect, useState } from "react";
import "./App.css";
import { Amplify } from "aws-amplify";
import { Authenticator } from "@aws-amplify/ui-react";
import "@aws-amplify/ui-react/styles.css";
import { getHello, getInfo } from "./utils/client";
import { AmplifyUser, AuthEventData } from "@aws-amplify/ui";

function App() {
  const [showLoginScreen, setShowLoginScreen] = useState(false);

  useEffect(() => {
    getInfo().then((config) => {
      Amplify.configure({
        Auth: {
          ...config,
        },
      });
    });
  }, []);

  interface HomeScreenProps {
    user: AmplifyUser | undefined;
    signOut: ((data?: AuthEventData | undefined) => void) | undefined;
  }

  const HomeScreen = ({ user, signOut }: HomeScreenProps) => {
    const [input, setInput] = useState("");
    const [output, setOutput] = useState("");
    return (
      <div className="App-header">
        <h1>Tetris template - Kotlin</h1>
        <h4>
          Hello {user?.attributes!["given_name"]}{" "}
          {user?.attributes!["family_name"]}
        </h4>
        {signOut ? (
          <button onClick={signOut}>Sign out</button>
        ) : (
          <button onClick={() => setShowLoginScreen(true)}>Sign in</button>
        )}
        <div>Send to server:</div>
        <input onChange={(event) => setInput(event.target.value)}></input>
        <br />
        <button
          onClick={() =>
            getHello(
              { name: input },
              user?.getSignInUserSession()?.getIdToken()?.getJwtToken()!,
            ).then(
              (res) => setOutput(res.message!),
              (err) => console.log(err),
            )
          }
        >
          click me
        </button>
        <div>Server response</div>
        <div>{output}</div>
      </div>
    );
  };

  return showLoginScreen ? (
    <Authenticator
      variation="modal"
      loginMechanisms={["email"]}
      signUpAttributes={["email", "family_name", "given_name"]}
    >
      {({ signOut, user }) => <HomeScreen user={user} signOut={signOut} />}
    </Authenticator>
  ) : (
    <HomeScreen user={undefined} signOut={undefined} />
  );
}

export default App;
