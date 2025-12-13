import React from 'react';
import { Button } from 'primereact/button';
import keycloak from '../keycloak';
import './LoginPage.css';

const LandingPage = () => {
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
        <div className="login-card">
          <h2>Добро пожаловать</h2>
          <p>Для продолжения выполните вход через Keycloak.</p>
          <Button
            label="Войти через Keycloak"
            onClick={() => keycloak.login({ redirectUri: `${window.location.origin}/` })}
          />
        </div>
      </main>
    </div>
  );
};

export default LandingPage;

