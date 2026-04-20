import api from './api';
import type { AuthResponse } from '../types/auth';

export const login = (username: string, password: string) =>
  api.post<AuthResponse>('/api/v1/auth/login', { username, password });

export const logout = () =>
  api.post('/api/v1/auth/logout');

export const refresh = () =>
  api.post('/api/v1/auth/refresh');
