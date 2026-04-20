import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Container, Row, Col, Form, Button, Spinner } from 'react-bootstrap';
import type { Route } from './+types/menu';
import * as dishService from '../services/dishService';
import * as orderService from '../services/orderService';
import useAuthStore from '../stores/authStore';
import useLoadingStore from '../stores/loadingStore';
import type { Dish } from '../types/dish';

const PAGE_SIZE = 6;

const CATEGORY_LABELS: Record<string, string> = {
  STARTER: 'Entrantes',
  MEAT: 'Carnes',
  DESSERT: 'Postres',
  DRINK: 'Bebidas',
};

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Menú' }];
}

export async function clientLoader({ request }: Route.ClientLoaderArgs) {
  const url = new URL(request.url);
  const q = url.searchParams.get('q') ?? '';
  const category = url.searchParams.get('category') ?? '';

  useLoadingStore.getState().setLoading(true);
  try {
    const { data } = await dishService.getDishesPaged(0, PAGE_SIZE, {
      q: q || undefined,
      category: category || undefined,
      availableOnly: true,
    });
    return { page: data, q, category };
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function Menu({ loaderData }: Route.ComponentProps) {
  const { page: initialPage, q: initialQ, category: initialCategory } = loaderData;
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();

  const [dishes, setDishes] = useState<Dish[]>(initialPage.content);
  const [pageIndex, setPageIndex] = useState(0);
  const [isLast, setIsLast] = useState(initialPage.last);
  const [loadingMore, setLoadingMore] = useState(false);
  const [searchInput, setSearchInput] = useState(initialQ);
  const [addedIds, setAddedIds] = useState<Set<number>>(new Set());

  const handleLoadMore = async () => {
    setLoadingMore(true);
    try {
      const next = pageIndex + 1;
      const { data } = await dishService.getDishesPaged(next, PAGE_SIZE, {
        q: initialQ || undefined,
        category: initialCategory || undefined,
        availableOnly: true,
      });
      setDishes((prev) => [...prev, ...data.content]);
      setPageIndex(next);
      setIsLast(data.last);
    } finally {
      setLoadingMore(false);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    const params = new URLSearchParams();
    if (searchInput) params.set('q', searchInput);
    if (initialCategory) params.set('category', initialCategory);
    navigate(`/new/menu?${params.toString()}`);
  };

  const handleCategoryFilter = (cat: string) => {
    const params = new URLSearchParams();
    if (cat) params.set('category', cat);
    if (initialQ) params.set('q', initialQ);
    navigate(`/new/menu?${params.toString()}`);
  };

  const handleAddToCart = async (dishId: number) => {
    if (!isAuthenticated) {
      navigate('/new/login');
      return;
    }
    try {
      await orderService.addToCart(dishId, 1);
      setAddedIds((prev) => new Set(prev).add(dishId));
      setTimeout(() => setAddedIds((prev) => { const s = new Set(prev); s.delete(dishId); return s; }), 1500);
    } catch {
      // silently ignore, user can retry
    }
  };

  return (
    <section className="section" style={{ paddingTop: '40px', paddingBottom: '60px' }}>
      <Container>
        <div className="section-title">
          <h2>Menú</h2>
          <p>Nuestras carnes y especialidades a fuego lento</p>
        </div>

        {/* Filters + search */}
        <div className="d-flex align-items-center justify-content-between gap-3 flex-wrap mb-4">
          <div className="d-flex align-items-center gap-2 flex-wrap">
            <Button
              size="sm"
              variant={!initialCategory ? 'secondary' : 'outline-secondary'}
              onClick={() => handleCategoryFilter('')}
            >
              Todo
            </Button>
            {Object.entries(CATEGORY_LABELS).map(([key, label]) => (
              <Button
                key={key}
                size="sm"
                variant={initialCategory === key ? 'secondary' : 'outline-secondary'}
                onClick={() => handleCategoryFilter(key)}
              >
                {label}
              </Button>
            ))}
          </div>

          <Form onSubmit={handleSearch} className="d-flex gap-2" style={{ minWidth: '280px' }}>
            <Form.Control
              type="text"
              placeholder="Buscar por plato..."
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
            />
            <Button type="submit" variant="outline-light">
              <i className="bi bi-search" />
            </Button>
          </Form>
        </div>

        {/* Dish list */}
        <Row className="g-4">
          {dishes.length === 0 && (
            <Col>
              <p className="text-center" style={{ opacity: 0.75 }}>
                No hay platos disponibles con esos filtros.
              </p>
            </Col>
          )}
          {dishes.map((dish) => (
            <Col key={dish.id} lg={6}>
              <div
                className="d-flex align-items-start gap-3 p-3 rounded-3"
                style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}
              >
                <img
                  src={dish.image ? `/api/v1/images/${dish.image.id}/media` : 'https://placehold.co/80x80?text=FL'}
                  alt={dish.name}
                  style={{ width: 80, height: 80, objectFit: 'cover', flexShrink: 0 }}
                />
                <div className="flex-grow-1">
                  <div className="d-flex justify-content-between gap-3">
                    <h5 className="mb-1 fw-bold">{dish.name}</h5>
                    <span className="fw-bold" style={{ color: 'var(--accent-color)', whiteSpace: 'nowrap' }}>
                      {dish.price.toFixed(2)} €
                    </span>
                  </div>
                  <p className="mb-2" style={{ opacity: 0.85 }}>{dish.description}</p>
                  <div className="d-flex gap-3 align-items-center flex-wrap">
                    <a
                      className="small"
                      style={{ cursor: 'pointer', color: 'var(--accent-color)' }}
                      onClick={() => navigate(`/new/menu/${dish.id}`)}
                    >
                      <i className="bi bi-info-circle me-1" />Ver detalles
                    </a>
                    <Button
                      size="sm"
                      variant={addedIds.has(dish.id) ? 'success' : 'outline-light'}
                      onClick={() => handleAddToCart(dish.id)}
                    >
                      <i className={`bi ${addedIds.has(dish.id) ? 'bi-check-circle' : 'bi-plus-circle'} me-1`} />
                      {addedIds.has(dish.id) ? 'Añadido' : 'Añadir a comanda'}
                    </Button>
                  </div>
                </div>
              </div>
            </Col>
          ))}
        </Row>

        {/* Load more button */}
        {!isLast && (
          <div className="text-center mt-4">
            <Button variant="outline-light" onClick={handleLoadMore} disabled={loadingMore}>
              {loadingMore ? (
                <><Spinner animation="border" size="sm" className="me-2" />Cargando...</>
              ) : (
                <><i className="bi bi-plus-circle me-1" />Cargar más</>
              )}
            </Button>
          </div>
        )}

        <div className="mt-5">
          <Button variant="outline-light" onClick={() => navigate('/new/')}>
            <i className="bi bi-arrow-left me-2" />Volver al inicio
          </Button>
        </div>
      </Container>
    </section>
  );
}
