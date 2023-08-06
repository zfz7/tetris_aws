import React, {useState} from 'react';
import './App.css';
import {SayHelloInput, SayHelloOutput} from 'ts-client'

function App() {
    const [input, setInput] = useState("Enter input:");
    const [output, setOutput] = useState("");

    return (
        <div className="App">
            <header className="App-header">
                <h1>Tetris template</h1>
                <div>Send to server:</div>
                <input onChange={(event) => setInput(event.target.value)}></input>
                <br/>
                <button onClick={() =>
                    sayHelloApi({name: input}).then(
                        (res) => setOutput(res.message!),
                        (err) => console.log(err)
                    )}>
                    click me
                </button>
                <div>Server response</div>
                <div>{output}</div>
            </header>
        </div>
    );
}

const sayHelloApi = (input: SayHelloInput): Promise<SayHelloOutput> => {
    const url = `https://api.${window.location.hostname}/hello`
    return fetch(`${url}?` + new URLSearchParams({"name": input.name!}), {
        method: 'GET'
    }).then(
        (response) => response.json(),
        (err) => console.log(err)
    )
}

export default App;
