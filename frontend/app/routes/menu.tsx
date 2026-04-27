import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';
import { Container, Row, Col, Spinner } from 'react-bootstrap';
import type { Route } from './+types/menu';
import * as dishService from '../services/dishService';
import * as orderService from '../services/orderService';
import useAuthStore from '../stores/authStore';
import useLoadingStore from '../stores/loadingStore';
import useCartStore from '../stores/cartStore';
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
  const { increment } = useCartStore();

  // Extra dishes appended via "load more" — reset when loaderData changes (filter/search navigation)
  const [extraDishes, setExtraDishes] = useState<Dish[]>([]);
  const [pageIndex, setPageIndex] = useState(0);
  const [isLast, setIsLast] = useState(initialPage.last);
  const [loadingMore, setLoadingMore] = useState(false);
  const [searchInput, setSearchInput] = useState(initialQ);
  const [addedIds, setAddedIds] = useState<Set<number>>(new Set());

  useEffect(() => {
    setExtraDishes([]);
    setPageIndex(0);
    setIsLast(initialPage.last);
    setSearchInput(initialQ);
  }, [initialPage, initialQ]);

  const dishes = [...initialPage.content, ...extraDishes];

  const handleLoadMore = async () => {
    setLoadingMore(true);
    try {
      const next = pageIndex + 1;
      const { data } = await dishService.getDishesPaged(next, PAGE_SIZE, {
        q: initialQ || undefined,
        category: initialCategory || undefined,
        availableOnly: true,
      });
      setExtraDishes((prev) => [...prev, ...data.content]);
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
    navigate(`/menu?${params.toString()}`);
  };

  const handleCategoryFilter = (cat: string) => {
    const params = new URLSearchParams();
    if (cat) params.set('category', cat);
    if (initialQ) params.set('q', initialQ);
    navigate(`/menu?${params.toString()}`);
  };

  const handleAddToCart = async (dishId: number) => {
    try {
      await orderService.addToCart(dishId, 1);
      increment();
      setAddedIds((prev) => new Set(prev).add(dishId));
      setTimeout(() => setAddedIds((prev) => { const s = new Set(prev); s.delete(dishId); return s; }), 1500);
    } catch {
      // silently ignore
    }
  };

  return (
    <section className="section" style={{ paddingTop: '120px', paddingBottom: '60px' }}>
      <Container>
        {/* Filters + search */}
        <div className="d-flex align-items-center justify-content-between gap-3 flex-wrap mb-4">
          <div className="d-flex align-items-center gap-2 flex-wrap">
            <a
              className={`btn btn-sm ${!initialCategory ? 'btn-kds-primary' : 'btn-kds-secondary'}`}
              style={{ cursor: 'pointer' }}
              onClick={() => handleCategoryFilter('')}
            >
              Todo
            </a>
            {Object.entries(CATEGORY_LABELS).map(([key, label]) => (
              <a
                key={key}
                className={`btn btn-sm ${initialCategory === key ? 'btn-kds-primary' : 'btn-kds-secondary'}`}
                style={{ cursor: 'pointer' }}
                onClick={() => handleCategoryFilter(key)}
              >
                {label}
              </a>
            ))}
          </div>

          <form className="admin-search" onSubmit={handleSearch} style={{ minWidth: '280px' }}>
            <i className="bi bi-search" />
            <input
              type="text"
              className="form-control"
              placeholder="Buscar por plato..."
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
            />
          </form>
        </div>

        {/* Dish list */}
        <Row className="g-4" id="menuList">
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
                  src={dish.image ? `/api/v1/images/${dish.image.id}/media` : `${import.meta.env.BASE_URL}img/logo.png`}
                  alt={dish.name}
                  style={{ width: 80, height: 80, objectFit: 'cover', flexShrink: 0 }}
                />
                <div className="flex-grow-1">
                  <div className="d-flex justify-content-between gap-3">
                    <h5 className="mb-1 fw-bold" style={{ color: 'var(--heading-color)' }}>{dish.name}</h5>
                    <span className="fw-bold" style={{ color: 'var(--accent-color)', whiteSpace: 'nowrap' }}>
                      {dish.price.toFixed(2)}€
                    </span>
                  </div>
                  <p className="mb-2" style={{ color: 'var(--default-color)', opacity: 0.85 }}>{dish.description}</p>
                  <div className="d-flex gap-3 align-items-center flex-wrap">
                    <a
                      className="small menu-link"
                      style={{ cursor: 'pointer' }}
                      onClick={() => navigate(`/menu/${dish.id}`)}
                    >
                      <i className="bi bi-info-circle me-1" />Ver detalles
                    </a>
                    {isAuthenticated && (
                      <button
                        className={`btn btn-sm ${addedIds.has(dish.id) ? 'btn-success' : 'btn-kds-primary'}`}
                        onClick={() => handleAddToCart(dish.id)}
                      >
                        <i className={`bi ${addedIds.has(dish.id) ? 'bi-check-circle' : 'bi-plus-circle'} me-1`} />
                        {addedIds.has(dish.id) ? 'Añadido' : 'Añadir a comanda'}
                      </button>
                    )}
                  </div>
                </div>
              </div>
            </Col>
          ))}
        </Row>

        {/* Load more */}
        {!isLast && (
          <div className="text-center mt-4">
            <button className="btn btn-kds-secondary" onClick={handleLoadMore} disabled={loadingMore}>
              {loadingMore ? (
                <><Spinner animation="border" size="sm" className="me-2" />Cargando...</>
              ) : (
                <><i className="bi bi-plus-circle me-1" />Cargar más</>
              )}
            </button>
          </div>
        )}

        <div className="mt-5">
          <a className="btn btn-outline-light" style={{ cursor: 'pointer' }} onClick={() => navigate('/')}>
            <i className="bi bi-arrow-left me-2" />Volver al inicio
          </a>
        </div>
      </Container>
    </section>
  );
}
