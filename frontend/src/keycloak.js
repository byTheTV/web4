import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: process.env.REACT_APP_KEYCLOAK_URL || 'http://localhost:8081',
  realm: process.env.REACT_APP_KEYCLOAK_REALM || 'area-check',
  clientId: process.env.REACT_APP_KEYCLOAK_CLIENT_ID || 'area-check-frontend',
});

let initPromise = null;

export const initKeycloak = (options) => {
  if (!initPromise) {
    initPromise = keycloak.init(options).catch((err) => {
      initPromise = null;
      throw err;
    });
  }
  return initPromise;
};

export const resetKeycloak = () => {
  initPromise = null;
  if (keycloak.clearToken) {
    keycloak.clearToken();
  }
};

export default keycloak;

