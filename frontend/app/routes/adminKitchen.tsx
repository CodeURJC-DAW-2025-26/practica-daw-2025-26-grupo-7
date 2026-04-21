import { useState, useEffect, useRef } from 'react';
import { Container, Row, Col, Badge, Button } from 'react-bootstrap';
import type { Route } from './+types/adminKitchen';
import * as orderService from '../services/orderService';
import useLoadingStore from '../stores/loadingStore';
import { OrderStatus } from '../types/order';
import type { Order } from '../types/order';

const KITCHEN_STATUSES = [OrderStatus.SENT_TO_KITCHEN, OrderStatus.IN_PROGRESS, OrderStatus.READY];

const STATUS_CONFIG: Record<string, { label: string; bg: string; next?: string; nextLabel?: string }> = {
  [OrderStatus.SENT_TO_KITCHEN]: { label: 'En cocina', bg: 'warning', next: OrderStatus.IN_PROGRESS, nextLabel: 'Preparando' },
  [OrderStatus.IN_PROGRESS]: { label: 'Preparando', bg: 'info', next: OrderStatus.READY, nextLabel: 'Listo' },
  [OrderStatus.READY]: { label: 'Listo', bg: 'success', next: OrderStatus.DELIVERED, nextLabel: 'Entregar' },
};

const POLL_INTERVAL = 30000;

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Cocina' }];
}

export async function clientLoader() {
  useLoadingStore.getState().setLoading(true);
  try {
    const results = await Promise.all(
      KITCHEN_STATUSES.map((s) => orderService.getOrders(s).then((r) => r.data))
    );
    return results.flat();
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function AdminKitchen({ loaderData }: Route.ComponentProps) {
  const [orders, setOrders] = useState<Order[]>(loaderData ?? []);
  const [lastUpdated, setLastUpdated] = useState(new Date());
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const fetchOrders = async () => {
    try {
      const results = await Promise.all(
        KITCHEN_STATUSES.map((s) => orderService.getOrders(s).then((r) => r.data))
      );
      setOrders(results.flat());
      setLastUpdated(new Date());
    } catch {
      // keep previous data on error
    }
  };

  useEffect(() => {
    timerRef.current = setInterval(fetchOrders, POLL_INTERVAL);
    return () => { if (timerRef.current) clearInterval(timerRef.current); };
  }, []);

  const handleAdvance = async (id: number, nextStatus: string) => {
    const { data } = await orderService.updateOrderStatus(id, nextStatus);
    setOrders((prev) =>
      nextStatus === OrderStatus.DELIVERED
        ? prev.filter((o) => o.id !== id)
        : prev.map((o) => (o.id === id ? data : o))
    );
  };

  const columns = KITCHEN_STATUSES.map((status) => ({
    status,
    config: STATUS_CONFIG[status],
    orders: orders.filter((o) => o.status === status),
  }));

  return (
    <section className="section" style={{ paddingTop: '120px', paddingBottom: '60px' }}>
      <Container fluid className="px-4">
        <div className="d-flex align-items-end justify-content-between flex-wrap gap-2 mb-4">
          <div>
            <h2 className="mb-1">Vista cocina</h2>
            <p className="mb-0 small" style={{ opacity: 0.7 }}>
              Actualización automática cada 30s · Última: {lastUpdated.toLocaleTimeString('es-ES')}
            </p>
          </div>
          <Button variant="outline-light" onClick={fetchOrders} style={{ borderRadius: 999 }}>
            <i className="bi bi-arrow-clockwise me-2" />Actualizar
          </Button>
        </div>

        <Row className="g-4">
          {columns.map(({ status, config, orders: colOrders }) => (
            <Col key={status} md={4}>
              <div className="p-3 rounded-4 h-100" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)' }}>
                <div className="d-flex align-items-center gap-2 mb-3">
                  <Badge bg={config.bg} className="px-3 py-2">{config.label}</Badge>
                  <span className="small fw-bold ms-auto">{colOrders.length}</span>
                </div>

                <div className="d-flex flex-column gap-3">
                  {colOrders.map((order) => (
                    <div key={order.id} className="p-3 rounded-3" style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid var(--border-color)' }}>
                      <div className="d-flex justify-content-between align-items-center mb-2">
                        <span className="fw-bold">Pedido #{order.id}</span>
                        {order.tableNumber && (
                          <span className="small badge bg-secondary">Mesa {order.tableNumber}</span>
                        )}
                      </div>
                      <div className="small mb-2" style={{ opacity: 0.7 }}>
                        {new Date(order.createdAt).toLocaleTimeString('es-ES')}
                        {order.username && ` · ${order.username}`}
                      </div>

                      {order.items.map((item) => (
                        <div key={item.id} className="py-1 border-bottom" style={{ borderColor: 'var(--border-color) !important' }}>
                          <span className="fw-bold me-1" style={{ color: 'var(--accent-color)' }}>{item.quantity}×</span>
                          <span>{item.dishName}</span>
                          {item.meatPoint && (
                            <span className="ms-2 small badge bg-secondary">{item.meatPoint.replace('_', ' ')}</span>
                          )}
                          {item.kitchenNote && (
                            <div className="small mt-1" style={{ opacity: 0.7 }}>
                              <i className="bi bi-chat-left-text me-1" />{item.kitchenNote}
                            </div>
                          )}
                        </div>
                      ))}

                      {order.customerNote && (
                        <div className="small mt-2" style={{ opacity: 0.7 }}>
                          <i className="bi bi-chat-left-text me-1" />{order.customerNote}
                        </div>
                      )}

                      {config.next && (
                        <Button
                          variant="primary"
                          size="sm"
                          className="mt-3 w-100"
                          onClick={() => handleAdvance(order.id, config.next!)}
                          style={{ borderRadius: 999 }}
                        >
                          {config.next === OrderStatus.DELIVERED
                            ? <><i className="bi bi-check2 me-1" />Entregar</>
                            : <><i className="bi bi-arrow-right me-1" />{config.nextLabel}</>}
                        </Button>
                      )}
                    </div>
                  ))}

                  {colOrders.length === 0 && (
                    <p className="text-center small mb-0" style={{ opacity: 0.5 }}>Sin comandas</p>
                  )}
                </div>
              </div>
            </Col>
          ))}
        </Row>
      </Container>
    </section>
  );
}
