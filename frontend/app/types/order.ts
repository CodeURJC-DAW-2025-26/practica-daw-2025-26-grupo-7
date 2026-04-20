export enum OrderStatus {
  PENDING = 'PENDING',
  PREPARING = 'PREPARING',
  READY = 'READY',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
}

export interface OrderItem {
  id: number;
  dishId: number;
  dishName: string;
  dishPrice: number;
  quantity: number;
}

export interface Order {
  id: number;
  tableNumber: number;
  status: OrderStatus;
  totalPrice: number;
  createdAt: string;
  items: OrderItem[];
  userId: number | null;
  userUsername: string | null;
}
