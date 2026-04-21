import { Link } from 'react-router';
import { Container, Row, Col } from 'react-bootstrap';

export default function AppFooter() {
  return (
    <footer id="footer" className="footer">
      <Container className="footer-top">
        <Row className="gy-4">
          <Col lg={6} md={6} className="footer-about">
            <Link className="logo d-flex align-items-center" to="/">
              <span className="sitename">Fuego Lento</span>
            </Link>
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
          </Col>

          <Col lg={6} md={6} className="footer-links">
            <h4>Enlaces</h4>
            <ul>
              <li><Link to="/">Inicio</Link></li>
              <li><Link to="/#about">Nuestra brasa</Link></li>
              <li><Link to="/menu">Menu</Link></li>
              <li><Link to="/#gallery">Galería</Link></li>
              <li><Link to="/#contact">Contacto</Link></li>
            </ul>
          </Col>
        </Row>
      </Container>

      <Container className="copyright text-center mt-4">
        <p>© <span>2026</span> <strong className="px-1 sitename">Fuego Lento</strong> <span>— Todos los derechos reservados</span></p>
      </Container>
    </footer>
  );
}
