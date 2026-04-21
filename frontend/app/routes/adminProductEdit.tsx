import { useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { Container, Row, Col, Form, Button, Alert, Spinner } from 'react-bootstrap';
import type { Route } from './+types/adminProductEdit';
import * as dishService from '../services/dishService';
import useLoadingStore from '../stores/loadingStore';
import { DishCategory } from '../types/dish';
import type { Dish } from '../types/dish';

const CATEGORY_OPTIONS = [
  { value: DishCategory.STARTER, label: 'Entrante' },
  { value: DishCategory.MEAT, label: 'Carne' },
  { value: DishCategory.DESSERT, label: 'Postre' },
  { value: DishCategory.DRINK, label: 'Bebida' },
];

const ALLERGEN_OPTIONS = [
  'GLUTEN', 'LACTOSE', 'EGGS', 'FISH', 'SHELLFISH',
  'NUTS', 'PEANUTS', 'SOY', 'SESAME', 'CELERY',
  'MUSTARD', 'SULPHITES', 'LUPIN', 'MOLLUSCS',
];

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Editar plato' }];
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

export default function AdminProductEdit({ loaderData }: Route.ComponentProps) {
  const navigate = useNavigate();
  const dish = loaderData as Dish;

  const [name, setName] = useState(dish.name);
  const [description, setDescription] = useState(dish.description ?? '');
  const [price, setPrice] = useState(String(dish.price));
  const [category, setCategory] = useState(dish.category);
  const [available, setAvailable] = useState(dish.available);
  const [allergens, setAllergens] = useState<string[]>(dish.allergens ?? []);
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const toggleAllergen = (a: string) => {
    setAllergens((prev) => prev.includes(a) ? prev.filter((x) => x !== a) : [...prev, a]);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await dishService.updateDish(dish.id, {
        name, description, price: parseFloat(price), category: category as any, available, allergens,
      });
      if (imageFile) {
        await dishService.uploadDishImage(dish.id, imageFile);
      }
      navigate('/admin/products');
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Error al actualizar el plato.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="section" style={{ paddingTop: '40px', paddingBottom: '60px' }}>
      <Container>
        <Row className="justify-content-center">
          <Col lg={7} md={9}>
            <div className="d-flex align-items-center gap-3 mb-4">
              <Button variant="outline-light" size="sm" onClick={() => navigate('/admin/products')} style={{ borderRadius: 999 }}>
                <i className="bi bi-arrow-left" />
              </Button>
              <div>
                <h2 className="mb-0">Editar plato</h2>
                <p className="mb-0 small" style={{ opacity: 0.7 }}>#{dish.id} — {dish.name}</p>
              </div>
            </div>

            {dish.image && (
              <div className="mb-4">
                <img
                  src={`/api/v1/images/${dish.image.id}`}
                  alt={dish.name}
                  className="rounded-3"
                  style={{ maxHeight: 200, objectFit: 'cover' }}
                />
              </div>
            )}

            <div className="p-4 rounded-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
              {error && <Alert variant="danger" onClose={() => setError(null)} dismissible>{error}</Alert>}

              <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                  <Form.Label>Nombre <span style={{ color: 'var(--accent-color)' }}>*</span></Form.Label>
                  <Form.Control type="text" value={name} onChange={(e) => setName(e.target.value)} required />
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Descripción</Form.Label>
                  <Form.Control as="textarea" rows={3} value={description} onChange={(e) => setDescription(e.target.value)} />
                </Form.Group>

                <Row className="g-3 mb-3">
                  <Col sm={6}>
                    <Form.Label>Precio <span style={{ color: 'var(--accent-color)' }}>*</span></Form.Label>
                    <Form.Control type="number" step="0.01" min="0" value={price} onChange={(e) => setPrice(e.target.value)} required />
                  </Col>
                  <Col sm={6}>
                    <Form.Label>Categoría <span style={{ color: 'var(--accent-color)' }}>*</span></Form.Label>
                    <Form.Select value={category} onChange={(e) => setCategory(e.target.value as DishCategory)} required>
                      {CATEGORY_OPTIONS.map((c) => (
                        <option key={c.value} value={c.value}>{c.label}</option>
                      ))}
                    </Form.Select>
                  </Col>
                </Row>

                <Form.Group className="mb-3">
                  <Form.Check
                    type="switch"
                    id="available-switch"
                    label="Disponible en carta"
                    checked={available}
                    onChange={(e) => setAvailable(e.target.checked)}
                  />
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Alérgenos</Form.Label>
                  <div className="d-flex flex-wrap gap-2">
                    {ALLERGEN_OPTIONS.map((a) => (
                      <Form.Check
                        key={a}
                        type="checkbox"
                        id={`allergen-${a}`}
                        label={a}
                        checked={allergens.includes(a)}
                        onChange={() => toggleAllergen(a)}
                        className="small"
                      />
                    ))}
                  </div>
                </Form.Group>

                <Form.Group className="mb-4">
                  <Form.Label>Cambiar imagen</Form.Label>
                  <Form.Control
                    type="file"
                    accept="image/*"
                    onChange={(e) => setImageFile((e.target as HTMLInputElement).files?.[0] ?? null)}
                  />
                  <Form.Text style={{ opacity: 0.6 }}>Deja vacío para mantener la imagen actual.</Form.Text>
                </Form.Group>

                <div className="d-flex gap-3 flex-wrap">
                  <Button type="submit" variant="primary" disabled={submitting} style={{ borderRadius: 999 }}>
                    {submitting ? <Spinner animation="border" size="sm" /> : <><i className="bi bi-check2 me-2" />Guardar cambios</>}
                  </Button>
                  <Button variant="outline-light" onClick={() => navigate('/admin/products')} disabled={submitting} style={{ borderRadius: 999 }}>
                    Cancelar
                  </Button>
                </div>
              </Form>
            </div>
          </Col>
        </Row>
      </Container>
    </section>
  );
}
