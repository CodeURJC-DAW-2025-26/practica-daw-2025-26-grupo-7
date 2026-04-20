import { useNavigate } from 'react-router';
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
        <div className="container">
          <nav className="navbar navbar-expand-xl navbar-dark py-2">

            <a className="navbar-brand d-flex align-items-center" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/')}>
              <img src="/new/img/logo.png" alt="Logo Fuego Lento" style={{ maxHeight: '60px', marginRight: '14px' }} />
              <span className="sitename">Fuego Lento</span>
            </a>

            <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse"
              aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
              <span className="navbar-toggler-icon"></span>
            </button>

            <div className="collapse navbar-collapse" id="navbarCollapse">
              <ul className="navbar-nav mx-xl-auto justify-content-xl-center align-items-xl-center">
                <li className="nav-item"><a className="nav-link" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/')}>Inicio</a></li>
                <li className="nav-item"><a className="nav-link" href="/new/#about">Nuestra brasa</a></li>
                <li className="nav-item"><a className="nav-link" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/menu')}>Menú</a></li>
                <li className="nav-item"><a className="nav-link" href="/new/#gallery">Galería</a></li>
                <li className="nav-item"><a className="nav-link" href="/new/#contact">Contacto</a></li>
              </ul>

              <div className="d-flex flex-column flex-xl-row gap-2 gap-xl-3 ms-xl-auto mt-3 mt-xl-0 navbar-actions">
                {isAuthenticated ? (
                  <>
                    <a className="btn-login text-center position-relative" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/cart')} aria-label="Ir a tu comanda">
                      <i className="bi bi-cart me-1"></i> COMANDA
                    </a>
                    <a className="btn-login text-center" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/profile')}>
                      <i className="bi bi-person-circle me-1"></i> {user?.username}
                    </a>
                    {isAdmin && (
                      <div className="dropdown">
                        <button className="btn-login dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                          <i className="bi bi-shield-lock me-1"></i> Admin
                        </button>
                        <ul className="dropdown-menu dropdown-menu-end">
                          <li><a className="dropdown-item" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/admin/kitchen')}><i className="bi bi-fire me-2"></i>Cocina</a></li>
                          <li><a className="dropdown-item" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/admin/orders')}><i className="bi bi-list-check me-2"></i>Comandas</a></li>
                          <li><a className="dropdown-item" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/admin/products')}><i className="bi bi-box-seam me-2"></i>Productos</a></li>
                          <li><a className="dropdown-item" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/admin/users')}><i className="bi bi-people me-2"></i>Usuarios</a></li>
                          <li><a className="dropdown-item" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/admin/dashboard')}><i className="bi bi-graph-up-arrow me-2"></i>Dashboard</a></li>
                        </ul>
                      </div>
                    )}
                    <a className="btn-login text-center" style={{ cursor: 'pointer' }} onClick={handleLogout}>
                      <i className="bi bi-box-arrow-right me-1"></i> Salir
                    </a>
                  </>
                ) : (
                  <>
                    <a className="btn-login text-center" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/login')}>Iniciar sesión</a>
                    <a className="btn-book-a-table text-center" href="/new/#book-a-table">Reservar</a>
                  </>
                )}
              </div>
            </div>
          </nav>
        </div>
      </div>
    </header>
  );
}
