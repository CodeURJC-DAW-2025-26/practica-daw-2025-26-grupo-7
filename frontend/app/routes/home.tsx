import { useNavigate } from 'react-router';
import { Container, Row, Col, Form, Button } from 'react-bootstrap';
import type { Route } from './+types/home';
import * as dishService from '../services/dishService';
import useLoadingStore from '../stores/loadingStore';
import type { Dish } from '../types/dish';

export function meta({}: Route.MetaArgs) {
  return [
    { title: 'Fuego Lento | Cocina a la brasa' },
    { name: 'description', content: 'Fuego Lento: carnes a la brasa y carne a la piedra. Cocina de fuego, producto y sabor.' },
  ];
}

export async function clientLoader() {
  useLoadingStore.getState().setLoading(true);
  try {
    const { data } = await dishService.getDishesPaged(0, 3, { availableOnly: true });
    return data.content;
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function Home({ loaderData }: Route.ComponentProps) {
  const dishes: Dish[] = loaderData ?? [];
  const navigate = useNavigate();

  return (
    <>
      {/* Hero */}
      <section id="hero" className="hero section dark-background">
        <img src="/new/img/hero-bg.jpg" alt="Fuego Lento - Brasa" />
        <Container>
          <Row>
            <Col lg={8} className="d-flex flex-column align-items-center align-items-lg-start">
              <h2>Bienvenido a <span>Fuego Lento</span></h2>
              <p>Carnes a la brasa y a la piedra. Producto, fuego y paciencia.</p>
              <div className="d-flex mt-4 gap-3">
                <a className="cta-btn" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/menu')}>Ver Menú</a>
                <a className="cta-btn" href="#book-a-table">Reservar mesa</a>
              </div>
            </Col>
          </Row>
        </Container>
      </section>

      {/* About */}
      <section id="about" className="about section">
        <Container>
          <Row className="gy-4 align-items-center">
            <Col lg={6} className="order-1 order-lg-2">
              <img src="/new/img/fachada-restaurante.png" className="img-fluid about-img" alt="Nuestra brasa"
                style={{ width: '90%', borderRadius: '8px' }} />
            </Col>
            <Col lg={6} className="order-2 order-lg-1 content">
              <h3>Nuestra brasa</h3>
              <p className="fst-italic">En Fuego Lento cocinamos sin prisa: el fuego marca el ritmo y el sabor hace el resto.</p>
              <ul>
                <li><i className="bi bi-check2-all"></i> <span>Carne a la piedra para que elijas el punto perfecto.</span></li>
                <li><i className="bi bi-check2-all"></i> <span>Parrilla y brasa: cortes clásicos y opciones premium.</span></li>
                <li><i className="bi bi-check2-all"></i> <span>Producto de calidad, cocina sencilla y sabor auténtico.</span></li>
              </ul>
              <p>Nuestro objetivo es claro: que cada bocado sepa a fuego, a hogar y a buena compañía.</p>
            </Col>
          </Row>
        </Container>
      </section>

      {/* Featured dishes */}
      <section className="section" style={{ background: '#0f0f0f' }}>
        <Container>
          <div className="section-title">
            <h2>Destacados</h2>
            <p>Platos de temporada</p>
          </div>
          <Row className="gy-4">
            {dishes.map((dish) => (
              <Col key={dish.id} lg={4} md={6}>
                <div className="menu-item">
                  <img
                    src={dish.imageId != null ? `/api/v1/images/${dish.imageId}/media` : 'https://placehold.co/78x78?text=FL'}
                    className="menu-img"
                    alt={dish.name}
                  />
                  <div className="menu-content">
                    <a style={{ cursor: 'pointer' }} onClick={() => navigate(`/new/menu/${dish.id}`)}>{dish.name}</a>
                    <span>{dish.price.toFixed(2)} €</span>
                  </div>
                  <div className="menu-ingredients">{dish.description}</div>
                </div>
              </Col>
            ))}
          </Row>
          <div className="text-center mt-5">
            <a className="cta-btn" style={{ cursor: 'pointer' }} onClick={() => navigate('/new/menu')}>Ver carta completa</a>
          </div>
        </Container>
      </section>

      {/* Reservation form */}
      <section id="book-a-table" className="book-a-table section">
        <Container className="section-title">
          <h2>Reservas</h2>
          <p>Reserva tu mesa</p>
        </Container>
        <Container>
          <Form className="php-email-form">
            <Row className="gy-4">
              <Col lg={4} md={6}>
                <Form.Control type="text" name="name" placeholder="Nombre" required />
              </Col>
              <Col lg={4} md={6}>
                <Form.Control type="email" name="email" placeholder="Email" required />
              </Col>
              <Col lg={4} md={6}>
                <Form.Control type="text" name="phone" placeholder="Teléfono" required />
              </Col>
              <Col lg={4} md={6}>
                <Form.Control type="date" name="date" required />
              </Col>
              <Col lg={4} md={6}>
                <Form.Control type="time" name="time" required />
              </Col>
              <Col lg={4} md={6}>
                <Form.Control type="number" name="people" placeholder="Nº de personas" required />
              </Col>
            </Row>
            <Form.Group className="mt-3">
              <Form.Control as="textarea" name="message" rows={5} placeholder="Mensaje (opcional)" />
            </Form.Group>
            <div className="text-center mt-3">
              <Button type="submit">Solicitar reserva</Button>
            </div>
          </Form>
        </Container>
      </section>

      {/* Gallery */}
      <section id="gallery" className="gallery section">
        <Container className="section-title">
          <h2>Galería</h2>
          <p>Un vistazo a Fuego Lento</p>
        </Container>
        <div className="container-fluid">
          <Row className="g-0">
            {[
              { src: '/new/img/gallery/interior-restaurante.png', alt: 'Interior de Fuego Lento' },
              { src: '/new/img/gallery/brasa-parrilla.png', alt: 'Brasa en Fuego Lento' },
              { src: '/new/img/gallery/barra-vino.png', alt: 'Barra y vinos' },
              { src: '/new/img/gallery/carne-piedra.png', alt: 'Carne a la piedra' },
              { src: '/new/img/gallery/ambiente-brindis.png', alt: 'Ambiente y brindis' },
              { src: '/new/img/gallery/detalles-mesa.png', alt: 'Detalles de mesa' },
            ].map((item) => (
              <Col key={item.src} lg={4} md={6}>
                <div className="gallery-item">
                  <img src={item.src} alt={item.alt} className="img-fluid" />
                </div>
              </Col>
            ))}
          </Row>
        </div>
      </section>

      {/* Contact */}
      <section id="contact" className="contact section">
        <Container className="section-title">
          <h2>Contacto</h2>
          <p>Escríbenos o ven a vernos</p>
        </Container>
        <Container>
          <Row className="gy-4 align-items-stretch">
            <Col lg={4}>
              <div className="info-item d-flex">
                <i className="bi bi-geo-alt flex-shrink-0"></i>
                <div><h3>Ubicación</h3><p>Coslada, Madrid</p></div>
              </div>
              <div className="info-item d-flex">
                <i className="bi bi-clock flex-shrink-0"></i>
                <div><h3>Horario</h3><p>Lunes a Domingo<br />13:00 - 16:00 / 20:00 - 23:30</p></div>
              </div>
              <div className="info-item d-flex">
                <i className="bi bi-telephone flex-shrink-0"></i>
                <div><h3>Teléfono</h3><p>+34 999 999 999</p></div>
              </div>
              <div className="info-item d-flex">
                <i className="bi bi-envelope flex-shrink-0"></i>
                <div><h3>Email</h3><p>reservas@fuegolento.com</p></div>
              </div>
            </Col>
            <Col lg={8}>
              <div className="rounded-4 overflow-hidden h-100"
                style={{ border: '1px solid rgba(255,255,255,0.10)', background: 'rgba(255,255,255,0.03)' }}>
                <iframe
                  style={{ border: 0, width: '100%', height: '100%', minHeight: '340px', display: 'block' }}
                  src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3035.5016078641724!2d-3.561984!3d40.423032!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd42303b6b0b7a99%3A0x2e6b7e9e5f1b8d1f!2sCoslada%2C%20Madrid!5e0!3m2!1ses!2ses!4v1700000000000"
                  allowFullScreen
                  loading="lazy"
                  referrerPolicy="no-referrer-when-downgrade"
                  title="Mapa Fuego Lento"
                ></iframe>
              </div>
            </Col>
          </Row>
        </Container>
      </section>
    </>
  );
}
