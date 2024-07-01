import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
const props = {
    title: "Welcome to My React App",
    description: "This is a simple example of a functional component in React."
};
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App {...props}/>
  </React.StrictMode>,
)
