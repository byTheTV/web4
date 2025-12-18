import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: process.env.REACT_APP_KEYCLOAK_URL || 'http://localhost:8081',
  realm: process.env.REACT_APP_KEYCLOAK_REALM || 'area-check',
  clientId: process.env.REACT_APP_KEYCLOAK_CLIENT_ID || 'area-check-frontend',
});

let initPromise = null;

export const initKeycloak = (options) => {
  if (!initPromise) {
    console.log('Initializing Keycloak with options:', options);
    initPromise = keycloak.init(options).catch((err) => {
      console.error('Keycloak init promise failed:', err);
      console.error('Error details:', {
        message: err?.message,
        error: err?.error,
        error_description: err?.error_description
      });
      initPromise = null;
      throw err;
    });
  }
  return initPromise;
};

export const resetKeycloak = () => {
  initPromise = null;
  // Очищаем токены и состояние Keycloak
  if (keycloak.token) {
    keycloak.clearToken();
  }
  if (keycloak.refreshToken) {
    keycloak.clearRefreshToken();
  }
};

export default keycloak;

