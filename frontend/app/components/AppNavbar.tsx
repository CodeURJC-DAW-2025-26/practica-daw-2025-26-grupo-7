import { useNavigate, Link } from 'react-router';
import { Navbar, Nav, Dropdown, Container } from 'react-bootstrap';
import useAuthStore from '../stores/authStore';

export default function AppNavbar() {
  const navigate = useNavigate();
  const { user, isAuthenticated, logout } = useAuthStore();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const isAdmin = user?.roles.includes('ADMIN');

  return (
    <header id="header" className="header fixed-top">
      <div className="branding">
        <Navbar expand="xl" variant="dark" className="py-2">
          <Container>
            <Navbar.Brand className="d-flex align-items-center" style={{ cursor: 'pointer' }} onClick={() => navigate('/')}>
              <img src="/new/img/logo.png" alt="Logo Fuego Lento" style={{ maxHeight: '60px', marginRight: '14px' }} />
              <span className="sitename">Fuego Lento</span>
            </Navbar.Brand>

            <Navbar.Toggle aria-controls="navbarCollapse" />

            <Navbar.Collapse id="navbarCollapse">
              <Nav className="mx-xl-auto justify-content-xl-center align-items-xl-center">
                <Nav.Link as={Link} to="/">Inicio</Nav.Link>
                <Nav.Link as={Link} to="/#about">Nuestra brasa</Nav.Link>
                <Nav.Link as={Link} to="/menu">Menú</Nav.Link>
                <Nav.Link as={Link} to="/#gallery">Galería</Nav.Link>
                <Nav.Link as={Link} to="/#contact">Contacto</Nav.Link>
              </Nav>

              <div className="d-flex flex-column flex-xl-row gap-2 gap-xl-3 ms-xl-auto mt-3 mt-xl-0 navbar-actions">
                {isAuthenticated ? (
                  <>
                    <a className="btn-login text-center position-relative" style={{ cursor: 'pointer' }}
                      onClick={() => navigate('/cart')} aria-label="Ir a tu comanda">
                      <i className="bi bi-cart me-1"></i> COMANDA
                    </a>
                    <a className="btn-login text-center" style={{ cursor: 'pointer' }} onClick={() => navigate('/profile')}>
                      <i className="bi bi-person-circle me-1"></i> {user?.username}
                    </a>
                    {isAdmin && (
                      <Dropdown align="end">
                        <Dropdown.Toggle className="btn-login" variant="" id="admin-dropdown">
                          <i className="bi bi-shield-lock me-1"></i> Admin
                        </Dropdown.Toggle>
                        <Dropdown.Menu>
                          <Dropdown.Item onClick={() => navigate('/admin/kitchen')}>
                            <i className="bi bi-fire me-2"></i>Cocina
                          </Dropdown.Item>
                          <Dropdown.Item onClick={() => navigate('/admin/orders')}>
                            <i className="bi bi-list-check me-2"></i>Comandas
                          </Dropdown.Item>
                          <Dropdown.Item onClick={() => navigate('/admin/products')}>
                            <i className="bi bi-box-seam me-2"></i>Productos
                          </Dropdown.Item>
                          <Dropdown.Item onClick={() => navigate('/admin/users')}>
                            <i className="bi bi-people me-2"></i>Usuarios
                          </Dropdown.Item>
                          <Dropdown.Item onClick={() => navigate('/admin/dashboard')}>
                            <i className="bi bi-graph-up-arrow me-2"></i>Dashboard
                          </Dropdown.Item>
                        </Dropdown.Menu>
                      </Dropdown>
                    )}
                    <a className="btn-login text-center" style={{ cursor: 'pointer' }} onClick={handleLogout}>
                      <i className="bi bi-box-arrow-right me-1"></i> Salir
                    </a>
                  </>
                ) : (
                  <>
                    <a className="btn-login text-center" style={{ cursor: 'pointer' }} onClick={() => navigate('/login')}>
                      Iniciar sesión
                    </a>
                    <Link className="btn-book-a-table text-center" to="/#book-a-table">Reservar</Link>
                  </>
                )}
              </div>
            </Navbar.Collapse>
          </Container>
        </Navbar>
      </div>
    </header>
  );
}
