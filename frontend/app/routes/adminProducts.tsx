import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Container, Row, Col, Button, Badge, Form, Spinner } from 'react-bootstrap';
import type { Route } from './+types/adminProducts';
import * as dishService from '../services/dishService';
import useLoadingStore from '../stores/loadingStore';
import type { Dish } from '../types/dish';
import { DishCategory } from '../types/dish';

const CATEGORY_LABELS: Record<string, string> = {
  [DishCategory.STARTER]: 'Entrante',
  [DishCategory.MEAT]: 'Carne',
  [DishCategory.DESSERT]: 'Postre',
  [DishCategory.DRINK]: 'Bebida',
};

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Gestión de productos' }];
}

export async function clientLoader() {
  useLoadingStore.getState().setLoading(true);
  try {
    const { data } = await dishService.getDishes();
    return data;
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function AdminProducts({ loaderData }: Route.ComponentProps) {
  const navigate = useNavigate();
  const [dishes, setDishes] = useState<Dish[]>(loaderData ?? []);
  const [search, setSearch] = useState('');
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const filtered = dishes.filter((d) =>
    d.name.toLowerCase().includes(search.toLowerCase())
  );

  const handleDelete = async (id: number) => {
    if (!confirm('¿Eliminar este plato?')) return;
    setDeletingId(id);
    try {
      await dishService.deleteDish(id);
      setDishes((prev) => prev.filter((d) => d.id !== id));
    } finally {
      setDeletingId(null);
    }
  };

  const handleToggleAvailable = async (dish: Dish) => {
    const updated = await dishService.updateDish(dish.id, { available: !dish.available });
    setDishes((prev) => prev.map((d) => (d.id === dish.id ? updated.data : d)));
  };

  return (
    <section className="section" style={{ paddingTop: '40px', paddingBottom: '60px' }}>
      <Container>
        <div className="d-flex align-items-end justify-content-between flex-wrap gap-2 mb-4">
          <div>
            <h2 className="mb-1">Gestión de productos</h2>
            <p className="mb-0" style={{ opacity: 0.7 }}>{dishes.length} platos en la carta</p>
          </div>
          <Button variant="primary" onClick={() => navigate('/admin/products/new')} style={{ borderRadius: 999 }}>
            <i className="bi bi-plus-lg me-2" />Nuevo plato
          </Button>
        </div>

        <div className="mb-4">
          <Form.Control
            type="search"
            placeholder="Buscar por nombre..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ maxWidth: 360 }}
          />
        </div>

        <Row className="g-3">
          {filtered.map((dish) => (
            <Col key={dish.id} xs={12} md={6} xl={4}>
              <div className="p-3 rounded-4 h-100 d-flex flex-column" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
                {dish.image && (
                  <img
                    src={`/api/v1/images/${dish.image.id}`}
                    alt={dish.name}
                    className="rounded-3 mb-3"
                    style={{ width: '100%', height: 140, objectFit: 'cover' }}
                  />
                )}
                <div className="flex-grow-1">
                  <div className="d-flex justify-content-between align-items-start gap-2 mb-1">
                    <h5 className="mb-0 fw-bold">{dish.name}</h5>
                    <span className="fw-bold" style={{ color: 'var(--accent-color)', whiteSpace: 'nowrap' }}>
                      {dish.price.toFixed(2)} €
                    </span>
                  </div>
                  <div className="mb-2">
                    <Badge bg="secondary" className="me-1">{CATEGORY_LABELS[dish.category] ?? dish.category}</Badge>
                    <Badge bg={dish.available ? 'success' : 'danger'}>
                      {dish.available ? 'Disponible' : 'No disponible'}
                    </Badge>
                  </div>
                  {dish.description && (
                    <p className="small mb-2" style={{ opacity: 0.75, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                      {dish.description}
                    </p>
                  )}
                </div>
                <div className="d-flex gap-2 mt-3 flex-wrap">
                  <Button variant="outline-light" size="sm" onClick={() => navigate(`/new/admin/products/${dish.id}/edit`)} style={{ borderRadius: 999 }}>
                    <i className="bi bi-pencil me-1" />Editar
                  </Button>
                  <Button
                    variant={dish.available ? 'outline-warning' : 'outline-success'}
                    size="sm"
                    onClick={() => handleToggleAvailable(dish)}
                    style={{ borderRadius: 999 }}
                  >
                    <i className={`bi bi-${dish.available ? 'eye-slash' : 'eye'} me-1`} />
                    {dish.available ? 'Ocultar' : 'Mostrar'}
                  </Button>
                  <Button
                    variant="outline-danger"
                    size="sm"
                    onClick={() => handleDelete(dish.id)}
                    disabled={deletingId === dish.id}
                    style={{ borderRadius: 999 }}
                  >
                    {deletingId === dish.id ? <Spinner animation="border" size="sm" /> : <><i className="bi bi-trash3 me-1" />Eliminar</>}
                  </Button>
                </div>
              </div>
            </Col>
          ))}
          {filtered.length === 0 && (
            <Col xs={12}>
              <p style={{ opacity: 0.75 }}>No se encontraron platos.</p>
            </Col>
          )}
        </Row>
      </Container>
    </section>
  );
}
