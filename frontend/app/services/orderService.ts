import api from './api';
import type { Order } from '../types/order';

export const getMyOrders = () =>
  api.get<Order[]>('/api/v1/orders/my');

export const getOrderById = (id: number) =>
  api.get<Order>(`/api/v1/orders/${id}`);

export const getMyCart = () =>
  api.get<Order>('/api/v1/orders/my-cart');

export const addToCart = (dishId: number, quantity: number) =>
  api.post<Order>('/api/v1/orders/cart/items', { dishId, quantity });

export const updateCartItem = (dishId: number, quantity: number) =>
  api.put<Order>(`/api/v1/orders/cart/items/${dishId}`, { quantity });

export const removeCartItem = (dishId: number) =>
  api.delete<Order>(`/api/v1/orders/cart/items/${dishId}`);

export const clearCart = () =>
  api.delete<Order>('/api/v1/orders/cart');

export const setTableNumber = (tableNumber: number) =>
  api.put<Order>('/api/v1/orders/cart/table', { tableNumber });

export const setCustomerNote = (note: string) =>
  api.put<Order>('/api/v1/orders/cart/customer-note', { value: note });

export const setItemComment = (dishId: number, comment: string) =>
  api.put<Order>(`/api/v1/orders/cart/items/${dishId}/comment`, { value: comment });

export const setItemMeatPoint = (dishId: number, meatPoint: string) =>
  api.put<Order>(`/api/v1/orders/cart/items/${dishId}/meat-point`, { value: meatPoint });

export const submitCart = () =>
  api.put<Order>('/api/v1/orders/cart/status', null, { params: { value: 'SENT_TO_KITCHEN' } });

export const getOrders = (status?: string) =>
  api.get<Order[]>('/api/v1/orders', { params: status ? { status } : undefined });

export const updateOrderStatus = (id: number, status: string) =>
  api.put<Order>(`/api/v1/orders/${id}/status`, null, { params: { value: status } });

export const deleteOrder = (id: number) =>
  api.delete(`/api/v1/orders/${id}`);
