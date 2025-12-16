import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import keycloak, { initKeycloak, resetKeycloak } from '../../keycloak';

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
      const hasAuthParams =
        window.location.href.includes('code=') ||
        window.location.href.includes('session_state=');

      // Если есть параметры авторизации, сбрасываем инициализацию для переинициализации
      if (hasAuthParams && keycloakInitialized) {
        resetKeycloak();
        keycloakInitialized = false;
      }

      if (!keycloakInitialized) {
        const authenticated = await initKeycloak({
          onLoad: hasAuthParams ? 'login-required' : 'check-sso',
          checkLoginIframe: false,
          pkceMethod: 'S256',
          silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
        });

        keycloakInitialized = true;

        if (!authenticated) {
          return { token: null, username: null, authenticated: false };
        }
      }

      // Проверяем состояние после инициализации
      if (!keycloak.authenticated) {
        return { token: null, username: null, authenticated: false };
      }

      // Если есть параметры авторизации (после редиректа), ждем немного и обновляем токен
      // чтобы убедиться, что все данные (включая роли) загружены
      if (hasAuthParams) {
        // Небольшая задержка для обработки редиректа
        await new Promise(resolve => setTimeout(resolve, 100));
        
        // Обновляем токен, чтобы получить актуальные данные (роли и т.д.)
        try {
          await keycloak.updateToken(30);
        } catch (error) {
          console.warn('Failed to update token after redirect:', error);
          // Продолжаем работу даже если обновление токена не удалось
        }
        
        // Очищаем URL от параметров авторизации после успешной инициализации
        window.history.replaceState({}, document.title, window.location.pathname);
      }

      return {
        token: keycloak.token,
        username: extractUsername(),
        authenticated: true,
      };
    } catch (error) {
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
        state.loading = false;
        state.token = action.payload.token;
        state.username = action.payload.username;
        state.isAuthenticated = !!action.payload.authenticated;
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

