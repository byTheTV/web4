import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import resultReducer from './slices/resultSlice';

const store = configureStore({
  reducer: {
    auth: authReducer,
    result: resultReducer,
  },
});

export default store;

