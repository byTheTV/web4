import React from 'react';
import { Button } from 'primereact/button';
import { Card } from 'primereact/card';
import keycloak from '../keycloak';
import './LoginPage.css';

const LandingPage = () => {
  const handleLogin = () => {
    console.log('Login clicked, redirecting to Keycloak...');
    keycloak.login({
      redirectUri: `${window.location.origin}/`,
    });
  };

  const studentInfo = {
    name: 'Тарасов Владислав Павлович',
    group: 'P3219',
    variant: 'Вариант 8765',
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
          <h2>Добро пожаловать</h2>
          <p style={{ marginBottom: '20px', textAlign: 'center' }}>
            Для продолжения выполните вход через Keycloak.
          </p>
          <Button
            label="Войти через Keycloak"
            onClick={handleLogin}
            className="submit-button"
          />
        </Card>
      </main>
    </div>
  );
};

export default LandingPage;

