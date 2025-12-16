import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import resultReducer from './slices/resultSlice';
import adminReducer from './slices/adminSlice';

const store = configureStore({
  reducer: {
    auth: authReducer,
    result: resultReducer,
    admin: adminReducer,
  },
});

export default store;

