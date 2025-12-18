import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import keycloak, { initKeycloak } from '../../keycloak';

let keycloakInitialized = false;

const extractUsername = () =>
  keycloak.tokenParsed?.preferred_username ||
  keycloak.tokenParsed?.email ||
  keycloak.tokenParsed?.sub ||
  '';

export const initializeKeycloak = createAsyncThunk(
  'auth/initializeKeycloak',
  async (_, { rejectWithValue }) => {
    try {
      console.log('initializeKeycloak - keycloakInitialized:', keycloakInitialized);
      console.log('Current URL:', window.location.href);

      // Проверяем наличие auth параметров в hash
      const hashParams = new URLSearchParams(window.location.hash.substring(1));
      const hasCode = hashParams.has('code');
      const hasSessionState = hashParams.has('session_state');
      
      console.log('hasCode:', hasCode, 'hasSessionState:', hasSessionState);

      if (!keycloakInitialized) {
        let authenticated = false;
        
        try {
          // Если есть параметры авторизации, не используем onLoad
          // Keycloak сам поймет что это callback и обработает его
          const initConfig = {
            checkLoginIframe: false,
            pkceMethod: 'S256',
          };
          
          // Используем check-sso только если нет параметров авторизации
          if (!hasCode && !hasSessionState) {
            initConfig.onLoad = 'check-sso';
          }
          
          console.log('Initializing Keycloak with config:', initConfig);
          
          authenticated = await initKeycloak(initConfig);

          keycloakInitialized = true;

          console.log('Keycloak initialized successfully');
          console.log('  - authenticated:', authenticated);
          console.log('  - keycloak.authenticated:', keycloak.authenticated);
          console.log('  - keycloak.token:', keycloak.token ? 'exists' : 'null');
          console.log('  - keycloak.tokenParsed:', keycloak.tokenParsed);

          // Очищаем URL от hash параметров после успешной инициализации
          if (window.location.hash && authenticated) {
            console.log('Cleaning hash from URL');
            window.history.replaceState({}, document.title, window.location.pathname + window.location.search);
          }
        } catch (initError) {
          console.error('Keycloak init failed:', initError);
          keycloakInitialized = false;
          return { token: null, username: null, authenticated: false };
        }

        if (!authenticated) {
          console.log('Not authenticated after init, returning false state');
          return { token: null, username: null, authenticated: false };
        }
      } else {
        console.log('Keycloak already initialized, checking state...');
        console.log('  - keycloak.authenticated:', keycloak.authenticated);
        console.log('  - keycloak.token:', keycloak.token ? 'exists' : 'null');
      }

      if (!keycloak.authenticated) {
        console.log('Keycloak not authenticated after init, returning false state');
        return { token: null, username: null, authenticated: false };
      }

      const username = extractUsername();
      console.log('Returning authenticated state with username:', username);
      
      return {
        token: keycloak.token,
        username: username,
        authenticated: true,
      };
    } catch (error) {
      console.error('Keycloak initialization error (outer):', error);
      keycloakInitialized = false;
      return rejectWithValue(error?.message || 'Не удалось инициализировать Keycloak');
    }
  }
);

export const refreshKeycloakToken = createAsyncThunk(
  'auth/refreshKeycloakToken',
  async (_, { rejectWithValue }) => {
    try {
      if (!keycloakInitialized) {
        return rejectWithValue('Keycloak не инициализирован');
      }
      await keycloak.updateToken(30);
      return {
        token: keycloak.token,
        username: extractUsername(),
      };
    } catch (error) {
      await keycloak.login();
      return rejectWithValue(error?.message || 'Сессия истекла, требуется вход');
    }
  }
);

export const logoutFromKeycloak = createAsyncThunk('auth/logoutFromKeycloak', async () => {
  await keycloak.logout();
  return {};
});

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    token: null,
    username: null,
    isAuthenticated: false,
    loading: true,
    error: null,
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(initializeKeycloak.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(initializeKeycloak.fulfilled, (state, action) => {
        console.log('initializeKeycloak.fulfilled - payload:', action.payload);
        state.loading = false;
        state.token = action.payload.token;
        state.username = action.payload.username;
        state.isAuthenticated = !!action.payload.authenticated;
        console.log('State updated - isAuthenticated:', state.isAuthenticated);
      })
      .addCase(initializeKeycloak.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || null;
        state.isAuthenticated = false;
      })
      .addCase(refreshKeycloakToken.fulfilled, (state, action) => {
        state.token = action.payload.token;
        state.username = action.payload.username;
        state.isAuthenticated = true;
      })
      .addCase(refreshKeycloakToken.rejected, (state, action) => {
        state.error = action.payload;
        state.isAuthenticated = false;
      })
      .addCase(logoutFromKeycloak.fulfilled, (state) => {
        state.token = null;
        state.username = null;
        state.isAuthenticated = false;
        state.loading = false;
        state.error = null;
      });
  },
});

export const { clearError } = authSlice.actions;
export default authSlice.reducer;

