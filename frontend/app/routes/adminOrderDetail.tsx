import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Container, Row, Col, Badge, Button, Form } from 'react-bootstrap';
import type { Route } from './+types/adminOrderDetail';
import * as orderService from '../services/orderService';
import useLoadingStore from '../stores/loadingStore';
import { OrderStatus } from '../types/order';
import type { Order } from '../types/order';

const STATUS_CONFIG: Record<string, { label: string; bg: string }> = {
  [OrderStatus.PENDING]: { label: 'En carrito', bg: 'secondary' },
  [OrderStatus.SENT_TO_KITCHEN]: { label: 'En cocina', bg: 'warning' },
  [OrderStatus.IN_PROGRESS]: { label: 'Preparando', bg: 'info' },
  [OrderStatus.READY]: { label: 'Listo', bg: 'success' },
  [OrderStatus.DELIVERED]: { label: 'Entregado', bg: 'success' },
  [OrderStatus.CANCELLED]: { label: 'Cancelado', bg: 'danger' },
};

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Detalle comanda' }];
}

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  useLoadingStore.getState().setLoading(true);
  try {
    const { data } = await orderService.getOrderById(Number(params.id));
    return data;
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function AdminOrderDetail({ loaderData }: Route.ComponentProps) {
  const navigate = useNavigate();
  const [order, setOrder] = useState<Order>(loaderData as Order);

  const status = STATUS_CONFIG[order.status] ?? { label: order.status, bg: 'secondary' };

  const handleStatusChange = async (newStatus: string) => {
    const { data } = await orderService.updateOrderStatus(order.id, newStatus);
    setOrder(data);
  };

  return (
    <section className="section" style={{ paddingTop: '40px', paddingBottom: '60px' }}>
      <Container>
        <Row className="justify-content-center">
          <Col lg={8}>
            <div className="d-flex align-items-center gap-3 mb-4">
              <Button variant="outline-light" size="sm" onClick={() => navigate('/admin/orders')} style={{ borderRadius: 999 }}>
                <i className="bi bi-arrow-left" />
              </Button>
              <div>
                <h2 className="mb-0">Pedido #{order.id}</h2>
                <p className="mb-0 small" style={{ opacity: 0.7 }}>
                  {new Date(order.createdAt).toLocaleString('es-ES')}
                  {order.username && ` · ${order.username}`}
                  {order.tableNumber && ` · Mesa ${order.tableNumber}`}
                </p>
              </div>
              <Badge bg={status.bg} className="ms-auto">{status.label}</Badge>
            </div>

            {/* Items */}
            <div className="p-4 rounded-4 mb-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
              <h5 className="mb-3">Platos</h5>
              <hr style={{ borderColor: 'var(--border-color)' }} />
              {order.items.map((item) => (
                <div key={item.id} className="d-flex justify-content-between align-items-start py-2">
                  <div>
                    <span className="fw-bold me-2" style={{ color: 'var(--accent-color)' }}>{item.quantity}×</span>
                    <span className="fw-bold">{item.dishName}</span>
                    {item.meatPoint && (
                      <span className="ms-2 small badge bg-secondary">{item.meatPoint.replace('_', ' ')}</span>
                    )}
                    {item.kitchenNote && (
                      <div className="small mt-1" style={{ opacity: 0.7 }}>
                        <i className="bi bi-chat-left-text me-1" />Nota: {item.kitchenNote}
                      </div>
                    )}
                  </div>
                  <span className="fw-bold">{item.totalPrice.toFixed(2)} €</span>
                </div>
              ))}
              <hr style={{ borderColor: 'var(--border-color)' }} />
              {order.customerNote && (
                <div className="small mb-2" style={{ opacity: 0.75 }}>
                  <i className="bi bi-chat-left-text me-1" />Nota general: {order.customerNote}
                </div>
              )}
              <div className="d-flex justify-content-end">
                <span className="fw-bold" style={{ color: 'var(--accent-color)', fontSize: 20 }}>
                  Total: {order.totalPrice.toFixed(2)} €
                </span>
              </div>
            </div>

            {/* Status actions */}
            <div className="p-4 rounded-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
              <h5 className="mb-3">Cambiar estado</h5>
              <div className="d-flex flex-wrap gap-2">
                {Object.entries(STATUS_CONFIG)
                  .filter(([k]) => k !== order.status && k !== OrderStatus.PENDING)
                  .map(([k, v]) => (
                    <Button
                      key={k}
                      variant="outline-light"
                      size="sm"
                      onClick={() => handleStatusChange(k)}
                      style={{ borderRadius: 999 }}
                    >
                      Marcar como: {v.label}
                    </Button>
                  ))}
              </div>
              {order.status === OrderStatus.DELIVERED && (
                <div className="mt-3">
                  <Button
                    variant="outline-light"
                    size="sm"
                    href={`/api/v1/orders/${order.id}/invoice`}
                    style={{ borderRadius: 999 }}
                  >
                    <i className="bi bi-filetype-pdf me-1" />Descargar factura
                  </Button>
                </div>
              )}
            </div>
          </Col>
        </Row>
      </Container>
    </section>
  );
}
