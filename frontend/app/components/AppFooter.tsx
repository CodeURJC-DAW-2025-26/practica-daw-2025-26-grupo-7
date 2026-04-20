import { useNavigate } from 'react-router';

export default function AppFooter() {
  const navigate = useNavigate();

  return (
    <footer id="footer" className="footer">
      <div className="container footer-top">
        <div className="row gy-4">

          <div className="col-lg-6 col-md-6 footer-about">
            <a className="logo d-flex align-items-center" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/')}>
              <span className="sitename">Fuego Lento</span>
            </a>
            <div className="footer-contact pt-3">
              <p>Coslada, Madrid</p>
              <p className="mt-3"><strong>Teléfono:</strong> <span>+34 999 999 999</span></p>
              <p><strong>Email:</strong> <span>reservas@fuegolento.com</span></p>
            </div>
            <div className="social-links d-flex mt-4">
              <a href="#" aria-label="Instagram"><i className="bi bi-instagram"></i></a>
              <a href="#" aria-label="Facebook"><i className="bi bi-facebook"></i></a>
              <a href="#" aria-label="TikTok"><i className="bi bi-tiktok"></i></a>
            </div>
          </div>

          <div className="col-lg-6 col-md-6 footer-links">
            <h4>Enlaces</h4>
            <ul>
              <li><a style={{ cursor: 'pointer' }} onClick={() => navigate('/new/')}>Inicio</a></li>
              <li><a href="/new/#about">Nuestra brasa</a></li>
              <li><a style={{ cursor: 'pointer' }} onClick={() => navigate('/new/menu')}>Menú</a></li>
              <li><a href="/new/#gallery">Galería</a></li>
              <li><a href="/new/#contact">Contacto</a></li>
            </ul>
          </div>

        </div>
      </div>

      <div className="container copyright text-center mt-4">
        <p>© <span>2026</span> <strong className="px-1 sitename">Fuego Lento</strong> <span>— Todos los derechos reservados</span></p>
      </div>
    </footer>
  );
}
