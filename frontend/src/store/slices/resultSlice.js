import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import api from '../../api';

export const checkPoint = createAsyncThunk(
  'result/checkPoint',
  async ({ x, y, r }, { rejectWithValue }) => {
    try {
      console.log('Sending checkPoint request:', { x, y, r });
      const response = await api.post('/api/area/check', { x, y, r });
      console.log('checkPoint response:', response.data);
      return response.data;
    } catch (error) {
      console.error('checkPoint error:', error);
      console.error('Error response:', error.response?.data);
      console.error('Error status:', error.response?.status);
      return rejectWithValue(error.response?.data || 'Check failed');
    }
  }
);

export const fetchResults = createAsyncThunk(
  'result/fetchResults',
  async (_, { rejectWithValue }) => {
    try {
      console.log('Fetching results from /api/area/results');
      const response = await api.get('/api/area/results');
      console.log('Results fetched:', response.data);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch results:', error);
      return rejectWithValue(error.response?.data || 'Fetch failed');
    }
  }
);

const resultSlice = createSlice({
  name: 'result',
  initialState: {
    results: [],
    loading: false,
    error: null,
  },
  reducers: {
    clearResults: (state) => {
      state.results = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(checkPoint.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(checkPoint.fulfilled, (state, action) => {
        state.loading = false;
        state.results.unshift(action.payload);
      })
      .addCase(checkPoint.rejected, (state, action) => {
        state.loading = false;
        // Обрабатываем ошибку maxRadius для более понятного сообщения
        if (typeof action.payload === 'string' && action.payload.includes('exceeds maximum allowed value')) {
          state.error = 'Введенное значение R превышает максимально допустимое для вашего аккаунта';
        } else {
          state.error = action.payload;
        }
      })
      .addCase(fetchResults.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchResults.fulfilled, (state, action) => {
        state.loading = false;
        state.results = action.payload;
      })
      .addCase(fetchResults.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearResults } = resultSlice.actions;
export default resultSlice.reducer;

