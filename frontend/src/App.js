import React, { useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import MainPage from './pages/MainPage';
import { initializeKeycloak, refreshKeycloakToken, logoutFromKeycloak } from './store/slices/authSlice';
import keycloak from './keycloak';
import './App.css';

function App() {
  const dispatch = useDispatch();
  const { isAuthenticated, loading, error } = useSelector(state => state.auth);

  useEffect(() => {
    dispatch(initializeKeycloak());
    const interval = setInterval(() => dispatch(refreshKeycloakToken()), 50000);
    return () => clearInterval(interval);
  }, [dispatch]);

  if (loading) {
    return (
      <div className="App">
        <p>Подключение к Keycloak...</p>
      </div>
    );
  }

  if (error || !isAuthenticated) {
    return (
      <div className="App">
        <p>{error || 'Требуется вход через Keycloak'}</p>
        <button onClick={() => keycloak.login({ redirectUri: `${window.location.origin}/` })}>Войти</button>
        <button onClick={() => keycloak.register({ redirectUri: `${window.location.origin}/` })}>
          Регистрация
        </button>
        <button onClick={() => dispatch(logoutFromKeycloak())}>Выйти</button>
      </div>
    );
  }

  return (
    <div className="App">
      <Routes>
        <Route path="/main" element={<MainPage />} />
        <Route path="*" element={<Navigate to="/main" />} />
      </Routes>
    </div>
  );
}

export default App;

