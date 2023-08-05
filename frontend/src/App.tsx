import React, {useState} from 'react';
import logo from './logo.svg';
import './App.css';
import {SayHelloInput} from 'tetris-ts-client'

function App() {
  const [sayHello, setSayHello] = useState<SayHelloInput>({name:"hi"});
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
        <div>{sayHello.name}</div>
        <button onClick={()=> setSayHello({name:sayHello.name+"a"})}>click me</button>
      </header>
    </div>
  );
}

export default App;
