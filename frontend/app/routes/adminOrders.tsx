import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Container, Row, Col, Badge, Button, Form } from 'react-bootstrap';
import type { Route } from './+types/adminOrders';
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
  return [{ title: 'Fuego Lento | Gestión de comandas' }];
}

export async function clientLoader() {
  useLoadingStore.getState().setLoading(true);
  try {
    const { data } = await orderService.getOrders();
    return data;
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function AdminOrders({ loaderData }: Route.ComponentProps) {
  const navigate = useNavigate();
  const [orders, setOrders] = useState<Order[]>(loaderData ?? []);
  const [statusFilter, setStatusFilter] = useState('');

  const filtered = statusFilter ? orders.filter((o) => o.status === statusFilter) : orders;

  const handleStatusChange = async (id: number, newStatus: string) => {
    const { data } = await orderService.updateOrderStatus(id, newStatus);
    setOrders((prev) => prev.map((o) => (o.id === id ? data : o)));
  };

  return (
    <section className="section" style={{ paddingTop: '120px', paddingBottom: '60px' }}>
      <Container>
        <div className="d-flex align-items-end justify-content-between flex-wrap gap-2 mb-4">
          <div>
            <h2 className="mb-1">Gestión de comandas</h2>
            <p className="mb-0" style={{ opacity: 0.7 }}>{orders.length} comandas en total</p>
          </div>
          <Button variant="outline-light" onClick={() => navigate('/admin/kitchen')} style={{ borderRadius: 999 }}>
            <i className="bi bi-fire me-2" />Vista cocina
          </Button>
        </div>

        <div className="mb-4">
          <Form.Select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            style={{ maxWidth: 240 }}
          >
            <option value="">Todos los estados</option>
            {Object.entries(STATUS_CONFIG).map(([k, v]) => (
              <option key={k} value={k}>{v.label}</option>
            ))}
          </Form.Select>
        </div>

        <Row className="g-3">
          {filtered.map((order) => {
            const status = STATUS_CONFIG[order.status] ?? { label: order.status, bg: 'secondary' };
            return (
              <Col key={order.id} xs={12}>
                <div className="p-4 rounded-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
                  <div className="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3">
                    <div>
                      <span className="fw-bold me-2">Pedido #{order.id}</span>
                      {order.username && (
                        <span className="small me-2" style={{ opacity: 0.7 }}>
                          <i className="bi bi-person me-1" />{order.username}
                        </span>
                      )}
                      {order.tableNumber && (
                        <span className="small me-2" style={{ opacity: 0.7 }}>
                          <i className="bi bi-grid-3x3-gap me-1" />Mesa {order.tableNumber}
                        </span>
                      )}
                      <span className="small" style={{ opacity: 0.7 }}>
                        <i className="bi bi-clock me-1" />{new Date(order.createdAt).toLocaleString('es-ES')}
                      </span>
                    </div>
                    <div className="d-flex align-items-center gap-2">
                      <Badge bg={status.bg}>{status.label}</Badge>
                      <Button variant="outline-light" size="sm" onClick={() => navigate(`/admin/orders/${order.id}`)} style={{ borderRadius: 999 }}>
                        <i className="bi bi-eye" />
                      </Button>
                    </div>
                  </div>

                  <div className="mb-3">
                    {order.items.map((item) => (
                      <div key={item.id} className="d-flex justify-content-between py-1">
                        <span>
                          <span className="fw-bold me-2" style={{ color: 'var(--accent-color)' }}>{item.quantity}×</span>
                          {item.dishName}
                        </span>
                        <span>{(item.totalPrice ?? 0).toFixed(2)} €</span>
                      </div>
                    ))}
                  </div>

                  <div className="d-flex flex-wrap justify-content-between align-items-center gap-3">
                    <div className="fw-bold" style={{ color: 'var(--accent-color)' }}>
                      Total: {(order.totalPrice ?? 0).toFixed(2)} €
                    </div>
                    <div className="d-flex gap-2 flex-wrap">
                      {Object.entries(STATUS_CONFIG)
                        .filter(([k]) => k !== order.status && k !== OrderStatus.PENDING)
                        .map(([k, v]) => (
                          <Button
                            key={k}
                            variant="outline-light"
                            size="sm"
                            onClick={() => handleStatusChange(order.id, k)}
                            style={{ borderRadius: 999 }}
                          >
                            → {v.label}
                          </Button>
                        ))}
                    </div>
                  </div>
                </div>
              </Col>
            );
          })}
          {filtered.length === 0 && (
            <Col xs={12}>
              <p style={{ opacity: 0.75 }}>No hay comandas con este filtro.</p>
            </Col>
          )}
        </Row>
      </Container>
    </section>
  );
}
