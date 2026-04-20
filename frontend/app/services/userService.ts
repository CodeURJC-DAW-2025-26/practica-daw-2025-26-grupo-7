import api from './api';
import type { User } from '../types/user';

export const register = (username: string, email: string, password: string, birthDate: string) =>
  api.post<User>('/api/v1/users', { username, email, password, birthDate });

export const getMe = () =>
  api.get<User>('/api/v1/users/me');

export const updateMe = (data: { email?: string; birthDate?: string }) =>
  api.put<User>('/api/v1/users/me', data);

export const getAllUsers = (query?: string) =>
  api.get<User[]>('/api/v1/users', { params: query ? { query } : undefined });

export const getUserById = (id: number) =>
  api.get<User>(`/api/v1/users/${id}`);

export const banUser = (id: number) =>
  api.put(`/api/v1/users/${id}/ban`);

export const unbanUser = (id: number) =>
  api.put(`/api/v1/users/${id}/unban`);

export const deleteUser = (id: number) =>
  api.delete(`/api/v1/users/${id}`);
