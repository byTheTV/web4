import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Button } from 'primereact/button';
import { Message } from 'primereact/message';
import { Card } from 'primereact/card';
import { login, register, clearError } from '../store/slices/authSlice';
import './LoginPage.css';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isRegister, setIsRegister] = useState(false);
  
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { loading, error, isAuthenticated } = useSelector(state => state.auth);

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/main');
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    dispatch(clearError());
    
    if (isRegister) {
      await dispatch(register({ username, password }));
    } else {
      await dispatch(login({ username, password }));
    }
  };

  const studentInfo = {
    name: 'Тарасов Владислав Павлович',
    group: 'P3219',
    variant: 'Вариант 8765'
  };

  return (
    <div className="login-page">
      <header className="login-header">
        <h1>
          {studentInfo.name}, группа {studentInfo.group}, {studentInfo.variant}
        </h1>
      </header>
      
      <main className="login-main">
        <Card className="login-card">
          <h2>{isRegister ? 'Регистрация' : 'Вход'}</h2>
          
          {error && (
            <Message severity="error" text={error} className="error-message" />
          )}
          
          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-field">
              <label htmlFor="username">Логин:</label>
              <InputText
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                disabled={loading}
                className="form-input"
              />
            </div>
            
            <div className="form-field">
              <label htmlFor="password">Пароль:</label>
              <Password
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                disabled={loading}
                feedback={false}
                toggleMask
                className="form-input"
              />
            </div>
            
            <Button
              type="submit"
              label={isRegister ? 'Зарегистрироваться' : 'Войти'}
              loading={loading}
              disabled={loading}
              className="submit-button"
            />
          </form>
          
          <div className="switch-mode">
            <button
              type="button"
              onClick={() => {
                setIsRegister(!isRegister);
                dispatch(clearError());
              }}
              className="link-button"
            >
              {isRegister ? 'Уже есть аккаунт? Войти' : 'Нет аккаунта? Зарегистрироваться'}
            </button>
          </div>
        </Card>
      </main>
    </div>
  );
};

export default LoginPage;

