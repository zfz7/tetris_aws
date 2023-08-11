import React, {useState} from 'react';
import './App.css';
import {SayHelloInput, SayHelloOutput} from 'ts-client'
import {Amplify} from 'aws-amplify';
import {Authenticator} from '@aws-amplify/ui-react';
import '@aws-amplify/ui-react/styles.css';

Amplify.configure({
    Auth: {
        region: "us-west-2",
        userPoolId: "us-west-2_Z6q0XxKpE", //TODO do not hard code this
        userPoolWebClientId: "28nea2k4nbcjnameihjju3d0g", //TODO do not hard code this
        authenticationFlowType: "USER_PASSWORD_AUTH",
    }
});

function App() {
    const [input, setInput] = useState("Enter input:");
    const [output, setOutput] = useState("");

    return (
        <Authenticator
            variation="modal"
            loginMechanisms={['email']}
            signUpAttributes={[
                'email',
                'family_name',
                'given_name',
            ]}>
            {({signOut, user}) => (
                <div className="App-header">
                    <h1>Tetris template</h1>
                    <h4>Hello {user?.attributes!["given_name"]} {user?.attributes!["family_name"]}</h4>
                    <button onClick={signOut}>Sign out</button>
                    <div>Send to server:</div>
                    <input onChange={(event) => setInput(event.target.value)}></input>
                    <br/>
                    <button onClick={() =>
                        sayHelloApi({name: input}, user?.getSignInUserSession()?.getIdToken()?.getJwtToken()!).then(
                            (res) => setOutput(res.message!),
                            (err) => console.log(err)
                        )}>
                        click me
                    </button>
                    <div>Server response</div>
                    <div>{output}</div>
                </div>
            )}
        </Authenticator>
    );
}

const sayHelloApi = (input: SayHelloInput, bearerToken: string): Promise<SayHelloOutput> => {
    const url = `https://api.${window.location.hostname}/hello`
    return fetch(`${url}?` + new URLSearchParams({"name": input.name!}), {
        method: 'GET',
        headers: {
            "Authorization": `Bearer ${bearerToken}`,
        }
    }).then(
        (response) => response.json(),
        (err) => console.log(err)
    )
}

export default App;
