import { Link, useNavigate } from 'react-router';
import { Container, Row, Col } from 'react-bootstrap';
import type { Route } from './+types/profile';
import useAuthStore from '../stores/authStore';
import useCartStore from '../stores/cartStore';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Mi perfil' }];
}

export default function Profile({}: Route.ComponentProps) {
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();
  const { reset } = useCartStore();

  const handleLogout = async () => {
    await logout();
    reset();
    navigate('/');
  };

  return (
    <section id="profile" className="section profile-section">
      <Container>

        {/* Page header */}
        <div className="profile-page-header d-flex flex-wrap align-items-end justify-content-between gap-2">
          <div>
            <h2>Mi cuenta</h2>
            <div className="kds-muted">Gestiona tus datos y accesos rápidos</div>
          </div>
          <div className="profile-breadcrumb">
            <i className="bi bi-house-door me-1" />
            <Link to="/">Inicio</Link> / <span>Mi cuenta</span>
          </div>
        </div>

        <Row className="g-4 align-items-start mt-1">

          {/* Left: user data card */}
          <Col lg={7}>
            <div className="profile-card">

              <div className="profile-user-head">
                <div className="profile-avatar">
                  <i className="bi bi-person-fill" />
                </div>
                <div className="profile-user-meta">
                  <h3>{user?.username}</h3>
                  <small>Usuario registrado</small>
                </div>
              </div>

              <hr className="profile-sep" />

              <Row className="gy-3">
                <Col md={6}>
                  <div className="profile-field">
                    <i className="bi bi-envelope" />
                    <div>
                      <div className="profile-label">Email</div>
                      <div className="profile-value">{user?.email}</div>
                    </div>
                  </div>
                </Col>
                {user?.birthDate && (
                  <Col md={6}>
                    <div className="profile-field">
                      <i className="bi bi-calendar-event" />
                      <div>
                        <div className="profile-label">Fecha de nacimiento</div>
                        <div className="profile-value">
                          {new Date(user.birthDate).toLocaleDateString('es-ES')}
                        </div>
                      </div>
                    </div>
                  </Col>
                )}
                {user?.createdAt && (
                  <Col md={6}>
                    <div className="profile-field">
                      <i className="bi bi-person-check" />
                      <div>
                        <div className="profile-label">Miembro desde</div>
                        <div className="profile-value">
                          {new Date(user.createdAt).toLocaleDateString('es-ES')}
                        </div>
                      </div>
                    </div>
                  </Col>
                )}
              </Row>

              <div className="profile-actions">
                <a className="btn btn-outline-light profile-pill" style={{ cursor: 'pointer' }} onClick={() => navigate('/profile/edit')}>
                  <i className="bi bi-pencil-square me-1" /> Editar perfil
                </a>
                <a className="btn btn-outline-light profile-pill" style={{ cursor: 'pointer' }} onClick={() => navigate('/orders')}>
                  <i className="bi bi-receipt me-1" /> Mis pedidos
                </a>
              </div>

            </div>
          </Col>

          {/* Right: quick actions + info */}
          <Col lg={5}>
            <div className="profile-card profile-quick-actions mb-4">
              <h4 className="mb-3">Acciones rápidas</h4>
              <div className="d-grid gap-2">
                <a className="btn btn-primary" style={{ cursor: 'pointer' }} onClick={() => navigate('/menu')}>
                  <i className="bi bi-fire me-1" /> Pedir desde la carta
                </a>
                <Link className="btn btn-outline-light profile-pill" to="/#book-a-table">
                  <i className="bi bi-calendar2-check me-1" /> Reservar mesa
                </Link>
                <button className="btn btn-outline-light profile-pill" onClick={handleLogout}>
                  <i className="bi bi-box-arrow-right me-1" /> Cerrar sesión
                </button>
              </div>
            </div>

            <div className="profile-card profile-help">
              <h4 className="mb-2">Tu mesa digital</h4>
              <p>Desde aquí podrás acceder a tus pedidos y gestionar reservas y datos personales.</p>
            </div>
          </Col>

        </Row>
      </Container>
    </section>
  );
}
