export enum OrderStatus {
  PENDING = 'PENDING',
  SENT_TO_KITCHEN = 'SENT_TO_KITCHEN',
  IN_PROGRESS = 'IN_PROGRESS',
  READY = 'READY',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
}

export interface OrderItem {
  id: number;
  dishId: number;
  dishName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  kitchenNote: string | null;
  meatPoint: string | null;
}

export interface Order {
  id: number;
  createdAt: string;
  status: OrderStatus;
  userId: number | null;
  username: string | null;
  items: OrderItem[];
  totalPrice: number;
  tableNumber: number | null;
  customerNote: string | null;
}
