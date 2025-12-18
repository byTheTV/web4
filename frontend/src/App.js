import React, { useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import MainPage from './pages/MainPage';
import AdminPage from './pages/AdminPage';
import LandingPage from './pages/LandingPage';
import { initializeKeycloak, refreshKeycloakToken } from './store/slices/authSlice';
import keycloak from './keycloak';
import './App.css';

const getUserRoles = () => {
  const realmRoles = keycloak.tokenParsed?.realm_access?.roles || [];
  const clientId = process.env.REACT_APP_KEYCLOAK_CLIENT_ID || 'area-check-frontend';
  const clientRoles = keycloak.tokenParsed?.resource_access?.[clientId]?.roles || [];
  return Array.from(new Set([...realmRoles, ...clientRoles]));
};

// Компонент для защиты маршрута USER
const ProtectedUserRoute = ({ children }) => {
  const { isAuthenticated, loading } = useSelector(state => state.auth);

  if (loading) {
    return (
      <div className="App">
        <p>Подключение к Keycloak...</p>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  const roles = getUserRoles();
  if (!roles.includes('USER')) {
    // Если пользователь авторизован, но нет роли USER, все равно разрешаем доступ
    // (возможно, роль еще не назначена администратором)
    // В production это должно быть настроено через default roles в Keycloak
    console.warn('User authenticated but USER role not found. Allowing access anyway.');
    return children;
  }

  return children;
};

// Компонент для защиты маршрута ADMIN
const ProtectedAdminRoute = ({ children }) => {
  const { isAuthenticated, loading } = useSelector(state => state.auth);

  if (loading) {
    return (
      <div className="App">
        <p>Подключение к Keycloak...</p>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  const roles = getUserRoles();
  if (!roles.includes('ADMIN')) {
    return <Navigate to="/" replace />;
  }

  return children;
};

// Компонент для редиректа авторизованных пользователей с главной страницы
const HomeRoute = () => {
  const { isAuthenticated, loading } = useSelector(state => state.auth);

  React.useEffect(() => {
    console.log('HomeRoute - isAuthenticated:', isAuthenticated, 'loading:', loading, 'keycloak.authenticated:', keycloak.authenticated);
  }, [isAuthenticated, loading]);

  if (loading) {
    return (
      <div className="App">
        <p>Подключение к Keycloak...</p>
      </div>
    );
  }

  // Если авторизован, определяем куда редиректить
  if (isAuthenticated && keycloak.authenticated) {
    const roles = getUserRoles();
    console.log('Redirecting authenticated user with roles:', roles);
    
    if (roles.includes('ADMIN')) {
      return <Navigate to="/admin" replace />;
    } else {
      // USER или любой авторизованный пользователь идет на /app
      return <Navigate to="/app" replace />;
    }
  }

  // Если не авторизован, показываем страницу входа
  return <LandingPage />;
};

function App() {
  const dispatch = useDispatch();
  const { loading } = useSelector(state => state.auth);
  const initializedRef = React.useRef(false);

  useEffect(() => {
    // Инициализируем Keycloak только один раз
    if (!initializedRef.current) {
      console.log('App - initializing Keycloak...');
      initializedRef.current = true;
      dispatch(initializeKeycloak());
    }
    
    const interval = setInterval(() => {
      // Обновляем токен только если Keycloak инициализирован и авторизован
      if (keycloak.authenticated) {
        dispatch(refreshKeycloakToken());
      }
    }, 50000);
    
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
        <Route path="/" element={<HomeRoute />} />
        <Route
          path="/app"
          element={
            <ProtectedUserRoute>
              <MainPage />
            </ProtectedUserRoute>
          }
        />
        <Route
          path="/admin"
          element={
            <ProtectedAdminRoute>
              <AdminPage />
            </ProtectedAdminRoute>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}

export default App;

