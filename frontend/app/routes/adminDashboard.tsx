import { useNavigate } from 'react-router';
import { Container, Row, Col, Badge, Button } from 'react-bootstrap';
import type { Route } from './+types/adminDashboard';
import * as orderService from '../services/orderService';
import * as dishService from '../services/dishService';
import * as userService from '../services/userService';
import useLoadingStore from '../stores/loadingStore';
import { OrderStatus } from '../types/order';
import type { Order } from '../types/order';
import type { Dish } from '../types/dish';
import type { User } from '../types/user';

interface DashboardData {
  orders: Order[];
  dishes: Dish[];
  users: User[];
}

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Dashboard' }];
}

export async function clientLoader() {
  useLoadingStore.getState().setLoading(true);
  try {
    const [ordersRes, dishesRes, usersRes] = await Promise.all([
      orderService.getOrders(),
      dishService.getDishes(),
      userService.getAllUsers(),
    ]);
    return { orders: ordersRes.data, dishes: dishesRes.data, users: usersRes.data } as DashboardData;
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function AdminDashboard({ loaderData }: Route.ComponentProps) {
  const navigate = useNavigate();
  const { orders, dishes, users } = (loaderData as DashboardData) ?? { orders: [], dishes: [], users: [] };

  const activeOrders = orders.filter((o) =>
    [OrderStatus.SENT_TO_KITCHEN, OrderStatus.IN_PROGRESS, OrderStatus.READY].includes(o.status)
  );
  const todayOrders = orders.filter((o) => {
    const d = new Date(o.createdAt);
    const now = new Date();
    return d.toDateString() === now.toDateString();
  });
  const todayRevenue = todayOrders
    .filter((o) => o.status === OrderStatus.DELIVERED)
    .reduce((sum, o) => sum + o.totalPrice, 0);

  const availableDishes = dishes.filter((d) => d.available).length;

  const stats = [
    { icon: 'bi-fire', label: 'Comandas activas', value: activeOrders.length, color: 'var(--accent-color)', action: () => navigate('/admin/kitchen') },
    { icon: 'bi-receipt', label: 'Pedidos hoy', value: todayOrders.length, color: '#17a2b8', action: () => navigate('/admin/orders') },
    { icon: 'bi-cash-coin', label: 'Ingresos hoy', value: `${todayRevenue.toFixed(2)} €`, color: '#28a745', action: null },
    { icon: 'bi-egg-fried', label: 'Platos disponibles', value: availableDishes, color: '#ffc107', action: () => navigate('/admin/products') },
    { icon: 'bi-people', label: 'Usuarios', value: users.length, color: '#6c757d', action: () => navigate('/admin/users') },
  ];

  const recentOrders = [...orders]
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 5);

  const STATUS_CONFIG: Record<string, { label: string; bg: string }> = {
    [OrderStatus.PENDING]: { label: 'En carrito', bg: 'secondary' },
    [OrderStatus.SENT_TO_KITCHEN]: { label: 'En cocina', bg: 'warning' },
    [OrderStatus.IN_PROGRESS]: { label: 'Preparando', bg: 'info' },
    [OrderStatus.READY]: { label: 'Listo', bg: 'success' },
    [OrderStatus.DELIVERED]: { label: 'Entregado', bg: 'success' },
    [OrderStatus.CANCELLED]: { label: 'Cancelado', bg: 'danger' },
  };

  return (
    <section className="section" style={{ paddingTop: '40px', paddingBottom: '60px' }}>
      <Container>
        <div className="mb-4">
          <h2 className="mb-1">Dashboard</h2>
          <p className="mb-0" style={{ opacity: 0.7 }}>Resumen general del restaurante</p>
        </div>

        {/* Stats */}
        <Row className="g-3 mb-5">
          {stats.map((stat) => (
            <Col key={stat.label} xs={6} md={4} lg={2} style={{ minWidth: 160 }}>
              <div
                className="p-4 rounded-4 h-100 text-center"
                style={{
                  background: 'rgba(255,255,255,0.03)',
                  border: '1px solid var(--border-color)',
                  cursor: stat.action ? 'pointer' : 'default',
                }}
                onClick={stat.action ?? undefined}
              >
                <i className={`bi ${stat.icon} mb-2 d-block`} style={{ fontSize: 32, color: stat.color }} />
                <div className="fw-bold mb-1" style={{ fontSize: 24, color: stat.color }}>{stat.value}</div>
                <div className="small" style={{ opacity: 0.7 }}>{stat.label}</div>
              </div>
            </Col>
          ))}
        </Row>

        {/* Quick actions */}
        <Row className="g-3 mb-5">
          <Col xs={12}>
            <h4 className="mb-3">Accesos rápidos</h4>
          </Col>
          {[
            { icon: 'bi-fire', label: 'Vista cocina', path: '/admin/kitchen', variant: 'primary' },
            { icon: 'bi-receipt', label: 'Comandas', path: '/admin/orders', variant: 'outline-light' },
            { icon: 'bi-egg-fried', label: 'Productos', path: '/admin/products', variant: 'outline-light' },
            { icon: 'bi-people', label: 'Usuarios', path: '/admin/users', variant: 'outline-light' },
          ].map((action) => (
            <Col key={action.path} xs={6} sm={3}>
              <Button
                variant={action.variant as any}
                className="w-100"
                onClick={() => navigate(action.path)}
                style={{ borderRadius: 999 }}
              >
                <i className={`bi ${action.icon} me-2`} />{action.label}
              </Button>
            </Col>
          ))}
        </Row>

        {/* Recent orders */}
        <div>
          <h4 className="mb-3">Últimas comandas</h4>
          <div className="rounded-4" style={{ border: '1px solid var(--border-color)' }}>
            {recentOrders.map((order, i) => {
              const st = STATUS_CONFIG[order.status] ?? { label: order.status, bg: 'secondary' };
              return (
                <div
                  key={order.id}
                  className="d-flex align-items-center justify-content-between px-4 py-3"
                  style={{
                    borderBottom: i < recentOrders.length - 1 ? '1px solid var(--border-color)' : 'none',
                    cursor: 'pointer',
                  }}
                  onClick={() => navigate(`/new/admin/orders/${order.id}`)}
                >
                  <div>
                    <span className="fw-bold me-2">#{order.id}</span>
                    {order.username && <span className="small me-2" style={{ opacity: 0.7 }}>{order.username}</span>}
                    {order.tableNumber && <span className="small" style={{ opacity: 0.7 }}>Mesa {order.tableNumber}</span>}
                  </div>
                  <div className="d-flex align-items-center gap-3">
                    <span className="small" style={{ opacity: 0.7 }}>
                      {new Date(order.createdAt).toLocaleString('es-ES')}
                    </span>
                    <Badge bg={st.bg}>{st.label}</Badge>
                    <span className="fw-bold" style={{ color: 'var(--accent-color)' }}>
                      {order.totalPrice.toFixed(2)} €
                    </span>
                  </div>
                </div>
              );
            })}
            {recentOrders.length === 0 && (
              <div className="px-4 py-3" style={{ opacity: 0.6 }}>Sin comandas recientes.</div>
            )}
          </div>
        </div>
      </Container>
    </section>
  );
}
