import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import api from '../../api';

export const fetchAdminStats = createAsyncThunk(
  'admin/fetchAdminStats',
  async (date, { rejectWithValue }) => {
    try {
      const url = date ? `/api/admin/stats?date=${date}` : '/api/admin/stats';
      console.log('Fetching admin stats from:', url);
      const response = await api.get(url);
      console.log('Admin stats response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch admin stats:', error);
      return rejectWithValue(error.response?.data || 'Failed to fetch admin stats');
    }
  }
);

const adminSlice = createSlice({
  name: 'admin',
  initialState: {
    stats: [],
    loading: false,
    error: null,
  },
  reducers: {
    clearStats: (state) => {
      state.stats = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchAdminStats.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAdminStats.fulfilled, (state, action) => {
        state.loading = false;
        state.stats = action.payload;
      })
      .addCase(fetchAdminStats.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearStats } = adminSlice.actions;
export default adminSlice.reducer;
