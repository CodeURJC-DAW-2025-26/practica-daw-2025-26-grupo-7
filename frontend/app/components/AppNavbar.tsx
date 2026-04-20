import { useNavigate } from 'react-router';
import { Navbar, Nav, Dropdown, Container } from 'react-bootstrap';
import useAuthStore from '../stores/authStore';

export default function AppNavbar() {
  const navigate = useNavigate();
  const { user, isAuthenticated, logout } = useAuthStore();

  const handleLogout = async () => {
    await logout();
    navigate('/new/');
  };

  const isAdmin = user?.roles.includes('ADMIN');

  return (
    <header id="header" className="header fixed-top">
      <div className="branding">
        <Navbar expand="xl" variant="dark" className="py-2">
          <Container>
            <Navbar.Brand style={{ cursor: 'pointer' }} onClick={() => navigate('/new/')}>
              <img src="/new/img/logo.png" alt="Logo Fuego Lento" style={{ maxHeight: '60px', marginRight: '14px' }} />
              <span className="sitename">Fuego Lento</span>
            </Navbar.Brand>

            <Navbar.Toggle aria-controls="navbarCollapse" />

            <Navbar.Collapse id="navbarCollapse">
              <Nav className="mx-xl-auto justify-content-xl-center align-items-xl-center">
                <Nav.Link style={{ cursor: 'pointer' }} onClick={() => navigate('/new/')}>Inicio</Nav.Link>
                <Nav.Link href="/new/#about">Nuestra brasa</Nav.Link>
                <Nav.Link style={{ cursor: 'pointer' }} onClick={() => navigate('/new/menu')}>Menú</Nav.Link>
                <Nav.Link href="/new/#gallery">Galería</Nav.Link>
                <Nav.Link href="/new/#contact">Contacto</Nav.Link>
              </Nav>

              <div className="d-flex flex-column flex-xl-row gap-2 gap-xl-3 ms-xl-auto mt-3 mt-xl-0 navbar-actions">
                {isAuthenticated ? (
                  <>
                    <a className="btn-login text-center position-relative" style={{ cursor: 'pointer' }}
                      onClick={() => navigate('/new/cart')} aria-label="Ir a tu comanda">
                      <i className="bi bi-cart me-1"></i> COMANDA
                    </a>
                    <a className="btn-login text-center" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/profile')}>
                      <i className="bi bi-person-circle me-1"></i> {user?.username}
                    </a>
                    {isAdmin && (
                      <Dropdown align="end">
                        <Dropdown.Toggle className="btn-login" variant="" id="admin-dropdown">
                          <i className="bi bi-shield-lock me-1"></i> Admin
                        </Dropdown.Toggle>
                        <Dropdown.Menu>
                          <Dropdown.Item onClick={() => navigate('/new/admin/kitchen')}>
                            <i className="bi bi-fire me-2"></i>Cocina
                          </Dropdown.Item>
                          <Dropdown.Item onClick={() => navigate('/new/admin/orders')}>
                            <i className="bi bi-list-check me-2"></i>Comandas
                          </Dropdown.Item>
                          <Dropdown.Item onClick={() => navigate('/new/admin/products')}>
                            <i className="bi bi-box-seam me-2"></i>Productos
                          </Dropdown.Item>
                          <Dropdown.Item onClick={() => navigate('/new/admin/users')}>
                            <i className="bi bi-people me-2"></i>Usuarios
                          </Dropdown.Item>
                          <Dropdown.Item onClick={() => navigate('/new/admin/dashboard')}>
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
                    <a className="btn-login text-center" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/login')}>
                      Iniciar sesión
                    </a>
                    <a className="btn-book-a-table text-center" href="/new/#book-a-table">Reservar</a>
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
