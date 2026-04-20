import api from './api';
import type { Dish } from '../types/dish';
import type { Page } from '../types/page';

interface DishParams {
  q?: string;
  category?: string;
  availableOnly?: boolean;
}

export const getDishes = (params?: DishParams) =>
  api.get<Dish[]>('/api/v1/dishes', { params });

export const getDishesPaged = (page: number, size: number, params?: DishParams) =>
  api.get<Page<Dish>>('/api/v1/dishes/page', { params: { page, size, ...params } });

export const getDishById = (id: number) =>
  api.get<Dish>(`/api/v1/dishes/${id}`);

export const createDish = (dish: Partial<Dish>) =>
  api.post<Dish>('/api/v1/dishes', dish);

export const updateDish = (id: number, dish: Partial<Dish>) =>
  api.put<Dish>(`/api/v1/dishes/${id}`, dish);

export const uploadDishImage = (id: number, file: File) => {
  const form = new FormData();
  form.append('imageFile', file);
  return api.post(`/api/v1/dishes/${id}/image`, form);
};

export const deleteDish = (id: number) =>
  api.delete(`/api/v1/dishes/${id}`);
