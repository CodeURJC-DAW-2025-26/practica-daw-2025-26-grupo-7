import { useNavigate, useRouteError, isRouteErrorResponse } from 'react-router';
import { Container, Row, Col, Button } from 'react-bootstrap';

export function meta() {
  return [{ title: 'Fuego Lento | Página no encontrada' }];
}

export default function NotFound() {
  const navigate = useNavigate();
  const error = useRouteError();

  const is403 = isRouteErrorResponse(error) && error.status === 403;
  const is404 = !error || (isRouteErrorResponse(error) && error.status === 404);

  const code = is403 ? 403 : 404;
  const title = is403 ? 'Acceso denegado' : 'Página no encontrada';
  const message = is403
    ? 'No tienes permisos para acceder a este recurso.'
    : 'La página que buscas no existe o ha sido movida.';

  return (
    <section className="section" style={{ paddingTop: '120px', paddingBottom: '90px' }}>
      <Container>
        <Row className="justify-content-center">
          <Col lg={9} xl={8}>
            <div
              className="p-5 rounded-4 shadow-lg"
              style={{
                background: 'rgba(255,255,255,0.04)',
                border: '1px solid var(--border-color)',
                backdropFilter: 'blur(6px)',
                minHeight: 300,
              }}
            >
              <div className="d-flex align-items-center gap-4 mb-4">
                <div
                  className="rounded-circle d-flex align-items-center justify-content-center"
                  style={{
                    width: 64,
                    height: 64,
                    background: 'rgba(255,0,0,0.12)',
                    border: '1px solid rgba(255,0,0,0.35)',
                  }}
                >
                  <i className="bi bi-exclamation-triangle" style={{ fontSize: '1.8rem' }} />
                </div>
                <div>
                  <h2 className="mb-1 fw-bold" style={{ fontSize: '1.9rem' }}>{title}</h2>
                  <small style={{ opacity: 0.75, fontSize: '0.95rem' }}>Código {code}</small>
                </div>
              </div>

              <p className="mb-5" style={{ opacity: 0.9, fontSize: '1.05rem' }}>{message}</p>

              <div className="d-flex gap-3 flex-wrap">
                <Button
                  className="btn-fuego px-4 py-2"
                  style={{ borderRadius: 999 }}
                  onClick={() => navigate('/')}
                >
                  <i className="bi bi-house-door me-1" /> Inicio
                </Button>
                <Button
                  variant="outline-light"
                  className="px-4 py-2"
                  style={{ borderRadius: 999 }}
                  onClick={() => navigate('/menu')}
                >
                  <i className="bi bi-arrow-right-circle me-1" /> Ver menú
                </Button>
              </div>
            </div>
          </Col>
        </Row>
      </Container>
    </section>
  );
}
