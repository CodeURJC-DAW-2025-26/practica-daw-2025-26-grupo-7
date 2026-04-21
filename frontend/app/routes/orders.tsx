import { useNavigate } from 'react-router';
import { Container, Row, Col, Badge, Button } from 'react-bootstrap';
import type { Route } from './+types/orders';
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
  return [{ title: 'Fuego Lento | Mis pedidos' }];
}

export async function clientLoader() {
  useLoadingStore.getState().setLoading(true);
  try {
    const { data } = await orderService.getMyOrders();
    return data;
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function Orders({ loaderData }: Route.ComponentProps) {
  const orders: Order[] = loaderData ?? [];
  const navigate = useNavigate();

  return (
    <section className="section" style={{ paddingTop: '40px', paddingBottom: '60px' }}>
      <Container>
        <div className="d-flex align-items-end justify-content-between flex-wrap gap-2 mb-4">
          <div>
            <h2 className="mb-1">Mis pedidos</h2>
            <p className="mb-0" style={{ opacity: 0.7 }}>Historial de comandas realizadas</p>
          </div>
          <Button variant="primary" onClick={() => navigate('/menu')} style={{ borderRadius: 999 }}>
            <i className="bi bi-fire me-1" />Pedir de nuevo
          </Button>
        </div>

        {orders.length === 0 && (
          <p style={{ opacity: 0.75 }}>Todavía no tienes pedidos anteriores.</p>
        )}

        <Row className="g-4">
          {orders.map((order) => {
            const status = STATUS_CONFIG[order.status] ?? { label: order.status, bg: 'secondary' };
            return (
              <Col key={order.id} xs={12}>
                <div className="p-4 rounded-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>

                  {/* Header */}
                  <div className="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3">
                    <div>
                      <span className="fw-bold me-2">Pedido #{order.id}</span>
                      <span className="small me-2" style={{ opacity: 0.7 }}>
                        <i className="bi bi-clock me-1" />
                        {new Date(order.createdAt).toLocaleString('es-ES')}
                      </span>
                      {order.tableNumber && (
                        <span className="small" style={{ opacity: 0.7 }}>
                          <i className="bi bi-grid-3x3-gap me-1" />Mesa {order.tableNumber}
                        </span>
                      )}
                    </div>
                    <Badge bg={status.bg}>{status.label}</Badge>
                  </div>

                  <hr style={{ borderColor: 'var(--border-color)' }} />

                  {/* Items */}
                  <div className="mb-3">
                    {order.items.map((item) => (
                      <div key={item.id} className="d-flex justify-content-between align-items-center py-1">
                        <div>
                          <span className="fw-bold me-2" style={{ color: 'var(--accent-color)' }}>{item.quantity}×</span>
                          <span>{item.dishName}</span>
                          {item.meatPoint && (
                            <span className="ms-2 small" style={{ opacity: 0.7 }}>Punto: {item.meatPoint}</span>
                          )}
                          {item.kitchenNote && (
                            <span className="ms-2 small" style={{ opacity: 0.7 }}>Nota: {item.kitchenNote}</span>
                          )}
                        </div>
                        <span className="fw-bold">{item.totalPrice.toFixed(2)} €</span>
                      </div>
                    ))}
                  </div>

                  {/* Footer */}
                  <div className="d-flex flex-wrap justify-content-between align-items-center gap-3">
                    <div>
                      {order.customerNote && (
                        <small style={{ opacity: 0.7 }}>
                          <i className="bi bi-chat-left-text me-1" />{order.customerNote}
                        </small>
                      )}
                    </div>
                    <div className="d-flex align-items-center gap-3">
                      <div className="text-end">
                        <div className="small" style={{ opacity: 0.7 }}>Total</div>
                        <div className="fw-bold" style={{ color: 'var(--accent-color)', fontSize: 18 }}>
                          {order.totalPrice.toFixed(2)} €
                        </div>
                      </div>
                      {order.status === OrderStatus.DELIVERED && (
                        <Button
                          variant="outline-light"
                          size="sm"
                          href={`/api/v1/orders/${order.id}/invoice`}
                          style={{ borderRadius: 999 }}
                        >
                          <i className="bi bi-filetype-pdf me-1" />Factura
                        </Button>
                      )}
                    </div>
                  </div>
                </div>
              </Col>
            );
          })}
        </Row>

        <div className="mt-4">
          <Button variant="outline-light" onClick={() => navigate('/profile')} style={{ borderRadius: 999 }}>
            <i className="bi bi-arrow-left me-2" />Volver a mi cuenta
          </Button>
        </div>
      </Container>
    </section>
  );
}
