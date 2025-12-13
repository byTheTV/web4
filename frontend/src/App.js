import React, { useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import MainPage from './pages/MainPage';
import LandingPage from './pages/LandingPage';
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

  return (
    <div className="App">
      <Routes>
        <Route path="/" element={isAuthenticated ? <Navigate to="/main" /> : <LandingPage />} />
        <Route
          path="/main"
          element={isAuthenticated ? <MainPage /> : <Navigate to="/" />}
        />
        <Route path="*" element={<Navigate to={isAuthenticated ? '/main' : '/'} />} />
      </Routes>
    </div>
  );
}

export default App;

