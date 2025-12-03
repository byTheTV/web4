import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import LoginPage from './pages/LoginPage';
import MainPage from './pages/MainPage';
import './App.css';

function App() {
  const isAuthenticated = useSelector(state => state.auth.isAuthenticated);

  return (
    <div className="App">
      <Routes>
        <Route 
          path="/login" 
          element={isAuthenticated ? <Navigate to="/main" /> : <LoginPage />} 
        />
        <Route 
          path="/main" 
          element={isAuthenticated ? <MainPage /> : <Navigate to="/login" />} 
        />
        <Route path="/" element={<Navigate to={isAuthenticated ? "/main" : "/login"} />} />
      </Routes>
    </div>
  );
}

export default App;

