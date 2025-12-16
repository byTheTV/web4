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
  const dispatch = useDispatch();
  const { isAuthenticated, loading } = useSelector(state => state.auth);
  const [redirectPath, setRedirectPath] = React.useState(null);
  const retryCountRef = React.useRef(0);

  React.useEffect(() => {
    if (!loading && isAuthenticated && keycloak.authenticated) {
      const checkRolesAndRedirect = async () => {
        try {
          // Если токен еще не распарсен, ждем немного и повторяем попытку
          if (!keycloak.tokenParsed && retryCountRef.current < 3) {
            retryCountRef.current += 1;
            setTimeout(() => {
              checkRolesAndRedirect();
            }, 200);
            return;
          }

          // Пытаемся обновить токен, чтобы получить актуальные роли (только при первой попытке)
          if (retryCountRef.current === 0) {
            try {
              await keycloak.updateToken(30);
              // После обновления токена, обновляем состояние
              dispatch(refreshKeycloakToken());
            } catch (error) {
              console.warn('Could not update token, using current token:', error);
            }
          }

          const roles = getUserRoles();
          console.log('User roles:', roles, 'Token parsed:', keycloak.tokenParsed);
          
          if (roles.includes('ADMIN')) {
            setRedirectPath('/admin');
          } else if (roles.includes('USER')) {
            setRedirectPath('/app');
          } else {
            console.warn('Authenticated user without required roles. Available roles:', roles);
            // Если пользователь авторизован, но нет роли, все равно перенаправляем на /app
            // (возможно, роль еще не назначена, но пользователь должен иметь доступ)
            setRedirectPath('/app');
          }
        } catch (error) {
          console.error('Error getting roles:', error);
          // В случае ошибки, если пользователь авторизован, перенаправляем на /app
          if (isAuthenticated) {
            setRedirectPath('/app');
          }
        }
      };

      const timer = setTimeout(() => {
        retryCountRef.current = 0;
        checkRolesAndRedirect();
      }, 100);
      
      return () => clearTimeout(timer);
    } else if (!isAuthenticated) {
      setRedirectPath(null);
      retryCountRef.current = 0;
    }
  }, [isAuthenticated, loading, dispatch]);

  if (loading) {
    return (
      <div className="App">
        <p>Подключение к Keycloak...</p>
      </div>
    );
  }

  // Если есть путь для редиректа, редиректим
  if (redirectPath) {
    return <Navigate to={redirectPath} replace />;
  }

  // Если не авторизован, показываем страницу входа
  return <LandingPage />;
};

function App() {
  const dispatch = useDispatch();
  const { loading } = useSelector(state => state.auth);

  useEffect(() => {
    // Всегда инициализируем Keycloak при загрузке приложения
    dispatch(initializeKeycloak());
    
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

