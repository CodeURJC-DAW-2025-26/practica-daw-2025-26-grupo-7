import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Container, Row, Col, Badge, Button, Spinner } from 'react-bootstrap';
import type { Route } from './+types/menuDetail';
import * as dishService from '../services/dishService';
import * as orderService from '../services/orderService';
import useAuthStore from '../stores/authStore';
import useLoadingStore from '../stores/loadingStore';

const ALLERGEN_LABELS: Record<string, string> = {
  GLUTEN: 'Gluten',
  EGG: 'Huevo',
  MILK: 'Lácteos',
  NUTS: 'Frutos secos',
  FISH: 'Pescado',
  SHELLFISH: 'Marisco',
  SOY: 'Soja',
  SESAME: 'Sésamo',
};

const CATEGORY_LABELS: Record<string, string> = {
  STARTER: 'Entrante',
  MEAT: 'Carne',
  DESSERT: 'Postre',
  DRINK: 'Bebida',
};

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Detalle del plato' }];
}

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  useLoadingStore.getState().setLoading(true);
  try {
    const { data } = await dishService.getDishById(Number(params.id));
    return data;
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function MenuDetail({ loaderData }: Route.ComponentProps) {
  const dish = loaderData;
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  const [added, setAdded] = useState(false);
  const [adding, setAdding] = useState(false);

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    setAdding(true);
    try {
      await orderService.addToCart(dish.id, 1);
      setAdded(true);
      setTimeout(() => setAdded(false), 2000);
    } finally {
      setAdding(false);
    }
  };

  return (
    <section className="section" style={{ paddingTop: '120px', paddingBottom: '60px' }}>
      <Container>
        <Row className="g-4 align-items-stretch">
          {/* Image */}
          <Col lg={6}>
            <div
              className="rounded-4 overflow-hidden h-100"
              style={{ border: '1px solid var(--border-color)', background: 'rgba(255,255,255,0.03)' }}
            >
              <img
                src={dish.image ? `/api/v1/images/${dish.image.id}/media` : 'https://placehold.co/600x420?text=FL'}
                alt={dish.name}
                style={{ width: '100%', height: '100%', objectFit: 'cover', minHeight: '420px' }}
              />
            </div>
          </Col>

          {/* Details */}
          <Col lg={6}>
            <div
              className="h-100 p-4 rounded-4"
              style={{
                border: '1px solid var(--border-color)',
                background: 'rgba(255,255,255,0.03)',
                boxShadow: '0 10px 30px rgba(0,0,0,0.25)',
              }}
            >
              <div className="d-flex align-items-start justify-content-between gap-3 mb-2">
                <div>
                  <p className="mb-1" style={{ letterSpacing: '0.12em', textTransform: 'uppercase', opacity: 0.75 }}>
                    {CATEGORY_LABELS[dish.category] ?? dish.category}
                  </p>
                  <h2 className="mb-0" style={{ fontWeight: 900 }}>{dish.name}</h2>
                </div>
                <div className="text-end flex-shrink-0">
                  <p className="mb-1" style={{ opacity: 0.75 }}>Precio</p>
                  <div className="fw-bold" style={{ fontSize: 22, color: 'var(--accent-color)' }}>
                    {dish.price.toFixed(2)} €
                  </div>
                </div>
              </div>

              <p className="mb-4" style={{ opacity: 0.9 }}>{dish.description}</p>

              {/* Allergens */}
              <div className="mb-4">
                <h5 className="mb-2 fw-bold">Alérgenos</h5>
                {dish.allergens && dish.allergens.length > 0 ? (
                  <div className="d-flex flex-wrap gap-2">
                    {dish.allergens.map((a) => (
                      <Badge
                        key={a}
                        bg=""
                        style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid var(--border-color)', color: 'inherit' }}
                      >
                        {ALLERGEN_LABELS[a] ?? a}
                      </Badge>
                    ))}
                  </div>
                ) : (
                  <p className="mb-0" style={{ opacity: 0.8 }}>No se han especificado alérgenos para este plato.</p>
                )}
                <p className="mt-2 mb-0" style={{ opacity: 0.7, fontSize: 13 }}>
                  * Información orientativa. Consulta al personal para detalles actualizados.
                </p>
              </div>

              {/* Actions */}
              <div className="d-flex flex-wrap gap-2 justify-content-end">
                <Button variant="outline-light" onClick={() => navigate('/menu')}>
                  <i className="bi bi-arrow-left me-1" />Volver al menú
                </Button>
                <Button
                  variant={added ? 'success' : 'outline-light'}
                  onClick={handleAddToCart}
                  disabled={adding}
                >
                  {adding ? (
                    <Spinner animation="border" size="sm" />
                  ) : (
                    <><i className={`bi ${added ? 'bi-check-circle' : 'bi-plus-circle'} me-1`} />
                      {added ? '¡Añadido!' : 'Añadir a comanda'}</>
                  )}
                </Button>
              </div>
            </div>
          </Col>
        </Row>

        {/* Note */}
        <div
          className="mt-4 p-4 rounded-4"
          style={{ border: '1px solid var(--border-color)', background: 'rgba(255,255,255,0.03)' }}
        >
          <h5 className="fw-bold mb-2">Notas</h5>
          <p className="mb-0" style={{ opacity: 0.85 }}>
            Si tienes alergias o intolerancias, indícalo al hacer tu comanda.
            Podemos adaptar algunos platos según disponibilidad.
          </p>
        </div>
      </Container>
    </section>
  );
}
