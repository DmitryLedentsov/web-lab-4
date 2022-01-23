import React from 'react';
import {useState} from 'react';
import Title from '../Title';
import {useDispatch} from 'react-redux';
import { setToken } from '../../slices/tokenSlice.js';
import { useNavigate } from 'react-router-dom';


const LoginContainer = ({serverPort}) => {
  const [login, setLogin] = useState('')
  const [password, setPassword] = useState('')

  const dispatch = useDispatch()

  const navigate = useNavigate();

  let loginAction = (e) => {
  e.preventDefault()
  if(!validateFields(login, password)){
    alert("Login or password are invalid!");
    //todo: fix alert
  }

  let token = sendLoginRequest(serverPort, login, password).token;
  console.log("Resived token for autorization");
  dispatch(setToken(token)); //todo: check if token is valid
  
  navigate('/main', {replace: true});
  // window.open("/main");

  // setLogin(''); //todo: uncomment
  // setPassword('');
  }

  return <form className="login_form container" onSubmit={loginAction} >
    <Title text="Login Here"/>

    <div className='login field'>
        <label>Login</label>
        <input type="text" placeholder="Login" value={login}
          onChange={(e) => setLogin(e.target.value)}/>
    </div>

    <div className='passwod field'>
        <label>Password</label>
        <input type="text" placeholder="Password" value={password}
          onChange={(e) => setPassword(e.target.value)}/>
    </div>

    <input type="submit" value='Submit' className='btn btn-block'/>
  </form>;
};

export default LoginContainer;

let validateFields = (login, password) => {
  return login !== "" && password !== "";
}

let sendLoginRequest = async (port, login, password) => {
  let url = "http://localhost:"+ port +"/auth/login?" + new URLSearchParams({"login":login, "password":password});
  console.log("Sending GET request to url: " + url);
  const response = await fetch(url, {
    method: 'GET',
    mode: 'cors',
  });
  return await response.json();
}