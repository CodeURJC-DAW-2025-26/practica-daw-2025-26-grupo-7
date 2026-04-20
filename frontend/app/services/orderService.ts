import api from './api';
import type { Order } from '../types/order';

export const getMyOrders = () =>
  api.get<Order[]>('/api/v1/orders/my');

export const getOrderById = (id: number) =>
  api.get<Order>(`/api/v1/orders/${id}`);

export const getMyCart = () =>
  api.get<Order>('/api/v1/orders/my-cart');

export const addToCart = (dishId: number, quantity: number) =>
  api.post('/api/v1/orders/cart/items', { dishId, quantity });

export const updateCartItem = (dishId: number, quantity: number) =>
  api.put(`/api/v1/orders/cart/items/${dishId}`, { quantity });

export const removeCartItem = (dishId: number) =>
  api.delete(`/api/v1/orders/cart/items/${dishId}`);

export const clearCart = () =>
  api.delete('/api/v1/orders/cart');

export const setTableNumber = (tableNumber: number) =>
  api.put('/api/v1/orders/cart/table', { tableNumber });

export const submitCart = () =>
  api.put('/api/v1/orders/cart/status', null, { params: { value: 'PENDING' } });

export const getOrders = (status?: string) =>
  api.get<Order[]>('/api/v1/orders', { params: status ? { status } : undefined });

export const updateOrderStatus = (id: number, status: string) =>
  api.put(`/api/v1/orders/${id}/status`, null, { params: { value: status } });

export const deleteOrder = (id: number) =>
  api.delete(`/api/v1/orders/${id}`);
