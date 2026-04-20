import axios from 'axios';

const api = axios.create({
  baseURL: '',
  withCredentials: true,
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const url: string = error.config?.url ?? '';
    if (error.response?.status === 401 && !url.includes('/api/v1/auth/')) {
      try {
        await api.post('/api/v1/auth/refresh');
        return api.request(error.config);
      } catch {
        return Promise.reject(error);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
