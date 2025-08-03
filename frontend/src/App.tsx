import { Authenticator } from "@aws-amplify/ui-react";
import "@aws-amplify/ui-react/styles.css";
import { useState } from "react";
import "./App.css";
import { LoginWindow } from "./components/Login";
import { Home } from "./components/Home";

function App() {
  const [showLoginScreen, setShowLoginScreen] = useState(false);
  return showLoginScreen ? (
    // Login window should not be inside <Authenticator.Provider>
    <LoginWindow loginComplete={() => setShowLoginScreen(false)} />
  ) : (
    <Authenticator.Provider>
      <Home showLoginScreen={() => setShowLoginScreen(true)} />
    </Authenticator.Provider>
  );
}

export default App;
