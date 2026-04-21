import { useNavigate } from 'react-router';
import { Container, Row, Col, Button, Badge } from 'react-bootstrap';
import type { Route } from './+types/profile';
import useAuthStore from '../stores/authStore';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Mi perfil' }];
}

export default function Profile({}: Route.ComponentProps) {
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const isAdmin = user?.roles?.includes('ROLE_ADMIN');

  return (
    <section className="section" style={{ paddingTop: '40px', paddingBottom: '60px' }}>
      <Container>
        <Row className="justify-content-center">
          <Col lg={8}>

            {/* Header */}
            <div className="d-flex align-items-end justify-content-between flex-wrap gap-2 mb-4">
              <div>
                <h2 className="mb-1">Mi perfil</h2>
                <p className="mb-0" style={{ opacity: 0.7 }}>Gestiona tu cuenta</p>
              </div>
              {isAdmin && (
                <Badge bg="warning" text="dark" className="px-3 py-2">
                  <i className="bi bi-shield-check me-1" />Administrador
                </Badge>
              )}
            </div>

            {/* User data card */}
            <div className="p-4 rounded-4 mb-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
              <h4 className="mb-3">Datos de la cuenta</h4>
              <hr style={{ borderColor: 'var(--border-color)' }} />

              <Row className="g-3">
                <Col sm={6}>
                  <div className="small mb-1" style={{ opacity: 0.6 }}>Usuario</div>
                  <div className="fw-bold">{user?.username}</div>
                </Col>
                <Col sm={6}>
                  <div className="small mb-1" style={{ opacity: 0.6 }}>Correo electrónico</div>
                  <div className="fw-bold">{user?.email}</div>
                </Col>
                {user?.birthDate && (
                  <Col sm={6}>
                    <div className="small mb-1" style={{ opacity: 0.6 }}>Fecha de nacimiento</div>
                    <div className="fw-bold">
                      {new Date(user.birthDate).toLocaleDateString('es-ES')}
                    </div>
                  </Col>
                )}
                <Col sm={6}>
                  <div className="small mb-1" style={{ opacity: 0.6 }}>Miembro desde</div>
                  <div className="fw-bold">
                    {user?.createdAt ? new Date(user.createdAt).toLocaleDateString('es-ES') : '—'}
                  </div>
                </Col>
              </Row>

              <div className="mt-4">
                <Button
                  variant="outline-light"
                  onClick={() => navigate('/profile/edit')}
                  style={{ borderRadius: 999 }}
                >
                  <i className="bi bi-pencil me-2" />Editar perfil
                </Button>
              </div>
            </div>

            {/* Quick actions */}
            <div className="p-4 rounded-4 mb-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
              <h4 className="mb-3">Acciones rápidas</h4>
              <hr style={{ borderColor: 'var(--border-color)' }} />
              <Row className="g-2">
                <Col xs={12} sm={6} md={4}>
                  <Button
                    variant="primary"
                    className="w-100"
                    onClick={() => navigate('/orders')}
                    style={{ borderRadius: 999 }}
                  >
                    <i className="bi bi-list-check me-2" />Mis pedidos
                  </Button>
                </Col>
                <Col xs={12} sm={6} md={4}>
                  <Button
                    variant="outline-light"
                    className="w-100"
                    onClick={() => navigate('/menu')}
                    style={{ borderRadius: 999 }}
                  >
                    <i className="bi bi-fire me-2" />Ver menú
                  </Button>
                </Col>
                {isAdmin && (
                  <Col xs={12} sm={6} md={4}>
                    <Button
                      variant="outline-light"
                      className="w-100"
                      onClick={() => navigate('/admin/dashboard')}
                      style={{ borderRadius: 999 }}
                    >
                      <i className="bi bi-speedometer2 me-2" />Panel admin
                    </Button>
                  </Col>
                )}
              </Row>
            </div>

            {/* Logout */}
            <div className="d-flex gap-3 flex-wrap">
              <Button variant="outline-danger" onClick={handleLogout} style={{ borderRadius: 999 }}>
                <i className="bi bi-box-arrow-right me-2" />Cerrar sesión
              </Button>
            </div>

          </Col>
        </Row>
      </Container>
    </section>
  );
}
