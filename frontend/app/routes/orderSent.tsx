import { useNavigate } from 'react-router';
import { Container, Row, Col, Button } from 'react-bootstrap';
import type { Route } from './+types/orderSent';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Comanda enviada' }];
}

export default function OrderSent({}: Route.ComponentProps) {
  const navigate = useNavigate();

  return (
    <section className="section">
      <Container>
        <Row className="justify-content-center">
          <Col lg={8}>
            <div className="text-center py-5">
              <div
                className="mx-auto mb-4 d-flex align-items-center justify-content-center rounded-circle"
                style={{ width: 96, height: 96, background: 'rgba(139,30,30,0.15)', border: '2px solid var(--accent-color)' }}
              >
                <i className="bi bi-check2" style={{ fontSize: 48, color: 'var(--accent-color)' }} />
              </div>

              <h2 className="mb-3">Comanda enviada</h2>
              <p className="mb-4" style={{ opacity: 0.85 }}>
                Hemos recibido tu comanda y ya está en cola para cocina.
              </p>

              <div className="d-flex justify-content-center gap-3 flex-wrap">
                <Button variant="outline-light" onClick={() => navigate('/menu')} style={{ borderRadius: 999 }}>
                  <i className="bi bi-arrow-left me-2" />Volver al menú
                </Button>
                <Button variant="primary" onClick={() => navigate('/orders')} style={{ borderRadius: 999 }}>
                  <i className="bi bi-list-check me-2" />Mis pedidos
                </Button>
              </div>

              <div
                className="mt-4 p-3 rounded-3 text-center"
                style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)', maxWidth: 480, margin: '1.5rem auto 0' }}
              >
                <small style={{ opacity: 0.8 }}>
                  <i className="bi bi-info-circle me-2" />
                  Si necesitas modificar algo, avisa al personal antes de que la comanda entre en fuego.
                </small>
              </div>
            </div>
          </Col>
        </Row>
      </Container>
    </section>
  );
}
