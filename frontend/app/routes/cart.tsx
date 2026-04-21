import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Container, Row, Col, Button, Form, Modal, Spinner } from 'react-bootstrap';
import type { Route } from './+types/cart';
import * as orderService from '../services/orderService';
import useLoadingStore from '../stores/loadingStore';
import type { Order } from '../types/order';

const MEAT_POINTS = [
  { value: 'MUY_HECHO', label: 'Muy hecho' },
  { value: 'HECHO', label: 'Hecho' },
  { value: 'AL_PUNTO', label: 'Al punto' },
  { value: 'POCO_HECHO', label: 'Poco hecho' },
];

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Mi comanda' }];
}

export async function clientLoader() {
  useLoadingStore.getState().setLoading(true);
  try {
    const { data } = await orderService.getMyCart();
    return data;
  } catch {
    return null;
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function Cart({ loaderData }: Route.ComponentProps) {
  const navigate = useNavigate();
  const [cart, setCart] = useState<Order | null>(loaderData ?? null);
  const [showConfirm, setShowConfirm] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const refresh = async () => {
    try {
      const { data } = await orderService.getMyCart();
      setCart(data);
    } catch {
      setCart(null);
    }
  };

  const handleUpdateQuantity = async (dishId: number, quantity: number) => {
    if (quantity <= 0) {
      const { data } = await orderService.removeCartItem(dishId);
      setCart(data);
    } else {
      const { data } = await orderService.updateCartItem(dishId, quantity);
      setCart(data);
    }
  };

  const handleRemove = async (dishId: number) => {
    const { data } = await orderService.removeCartItem(dishId);
    setCart(data);
  };

  const handleClear = async () => {
    await orderService.clearCart();
    await refresh();
  };

  const handleTableChange = async (value: string) => {
    if (!value) return;
    const { data } = await orderService.setTableNumber(Number(value));
    setCart(data);
  };

  const handleCustomerNote = async (note: string) => {
    const { data } = await orderService.setCustomerNote(note);
    setCart(data);
  };

  const handleItemComment = async (dishId: number, comment: string) => {
    const { data } = await orderService.setItemComment(dishId, comment);
    setCart(data);
  };

  const handleMeatPoint = async (dishId: number, meatPoint: string) => {
    const { data } = await orderService.setItemMeatPoint(dishId, meatPoint);
    setCart(data);
  };

  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      await orderService.submitCart();
      setShowConfirm(false);
      navigate('/order-sent');
    } finally {
      setSubmitting(false);
    }
  };

  const hasItems = cart && cart.items && cart.items.length > 0;

  return (
    <section className="section" style={{ paddingTop: '40px', paddingBottom: '70px' }}>
      <Container>
        <Row className="g-4 align-items-start">

          {/* Left: items */}
          <Col lg={8}>
            <div className="p-4 rounded-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
              <div className="d-flex align-items-center justify-content-between gap-3 flex-wrap mb-3">
                <h3 className="mb-0">Resumen</h3>
                <Button variant="outline-light" size="sm" onClick={() => navigate('/menu')}>
                  <i className="bi bi-plus-lg me-2" />Seguir añadiendo
                </Button>
              </div>
              <hr style={{ borderColor: 'var(--border-color)' }} />

              {!hasItems && (
                <p className="mb-0" style={{ opacity: 0.75 }}>
                  Tu comanda está vacía. Añade platos desde el menú.
                </p>
              )}

              {cart?.items.map((item) => (
                <div key={item.id}>
                  <div className="d-flex gap-3 py-3">
                    <div className="flex-grow-1">
                      <div className="d-flex justify-content-between gap-3 mb-2">
                        <div>
                          <h5 className="mb-1 fw-bold">{item.dishName}</h5>
                        </div>
                        <div className="text-end">
                          <div className="fw-bold" style={{ color: 'var(--accent-color)' }}>
                            {item.unitPrice.toFixed(2)} €
                          </div>
                          <div className="small" style={{ opacity: 0.75 }}>Unidad</div>
                        </div>
                      </div>

                      <Row className="g-2 align-items-end">
                        {/* Quantity */}
                        <Col md={4}>
                          <Form.Label className="mb-1 small">Cantidad</Form.Label>
                          <div className="d-flex gap-2 align-items-center">
                            <Form.Control
                              type="number"
                              min={0}
                              defaultValue={item.quantity}
                              style={{ maxWidth: 90 }}
                              onBlur={(e) => handleUpdateQuantity(item.dishId, Number(e.target.value))}
                            />
                            <Button variant="outline-light" size="sm"
                              onClick={(e) => handleUpdateQuantity(item.dishId, Number((e.currentTarget.previousElementSibling as HTMLInputElement)?.value))}>
                              ✓
                            </Button>
                          </div>
                        </Col>

                        {/* Meat point */}
                        <Col md={4}>
                          <Form.Label className="mb-1 small">Punto de la carne</Form.Label>
                          <Form.Select
                            size="sm"
                            defaultValue={item.meatPoint ?? ''}
                            onChange={(e) => { if (e.target.value) handleMeatPoint(item.dishId, e.target.value); }}
                          >
                            <option value="">— Sin punto —</option>
                            {MEAT_POINTS.map((mp) => (
                              <option key={mp.value} value={mp.value}>{mp.label}</option>
                            ))}
                          </Form.Select>
                        </Col>

                        {/* Remove */}
                        <Col md={4} className="text-md-end">
                          <Button variant="outline-danger" size="sm" onClick={() => handleRemove(item.dishId)}>
                            <i className="bi bi-trash3 me-1" />Quitar
                          </Button>
                        </Col>

                        {/* Line total */}
                        <Col xs={12}>
                          <div className="d-flex justify-content-between align-items-center">
                            <span className="small" style={{ opacity: 0.8 }}>Total línea</span>
                            <span className="fw-bold" style={{ color: 'var(--accent-color)' }}>
                              {item.totalPrice.toFixed(2)} €
                            </span>
                          </div>
                        </Col>

                        {/* Item comment */}
                        <Col xs={12}>
                          <Form.Label className="mb-1 small">Comentario para cocina (opcional)</Form.Label>
                          <div className="d-flex gap-2">
                            <Form.Control
                              type="text"
                              size="sm"
                              defaultValue={item.kitchenNote ?? ''}
                              placeholder="Ej: sin sal, más hecho..."
                              onBlur={(e) => handleItemComment(item.dishId, e.target.value)}
                            />
                          </div>
                        </Col>
                      </Row>
                    </div>
                  </div>
                  <hr style={{ borderColor: 'var(--border-color)' }} />
                </div>
              ))}

              {hasItems && (
                <div className="d-flex justify-content-end">
                  <Button variant="outline-light" size="sm" onClick={handleClear}>
                    Vaciar comanda
                  </Button>
                </div>
              )}
            </div>
          </Col>

          {/* Right: summary + actions */}
          <Col lg={4}>
            <div className="p-4 rounded-4 mb-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
              <h4 className="mb-3">Total</h4>
              <div className="d-flex justify-content-between mb-2">
                <span style={{ opacity: 0.85 }}>Productos</span>
                <strong>{(cart?.totalPrice ?? 0).toFixed(2)} €</strong>
              </div>
              <div className="d-flex justify-content-between mb-3">
                <span style={{ opacity: 0.85 }}>Servicio</span>
                <strong>0,00 €</strong>
              </div>
              <hr style={{ borderColor: 'var(--border-color)' }} />
              <div className="d-flex justify-content-between align-items-center mb-4">
                <span className="fw-bold">Total</span>
                <span className="fw-bold" style={{ color: 'var(--accent-color)', fontSize: 20 }}>
                  {(cart?.totalPrice ?? 0).toFixed(2)} €
                </span>
              </div>

              {/* Table number */}
              <Form.Label className="mb-1">Número de mesa <span style={{ color: 'var(--accent-color)' }}>*</span></Form.Label>
              <Form.Select
                value={cart?.tableNumber ?? ''}
                onChange={(e) => handleTableChange(e.target.value)}
                className="mb-1"
              >
                <option value="">Selecciona tu mesa (1 - 20)</option>
                {Array.from({ length: 20 }, (_, i) => i + 1).map((n) => (
                  <option key={n} value={n}>Mesa {n}</option>
                ))}
              </Form.Select>
              <small style={{ opacity: 0.7 }}>El número está indicado en la esquina de la mesa.</small>

              {/* General note */}
              <Form.Label className="mt-3 mb-1">Comentario general (opcional)</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                defaultValue={cart?.customerNote ?? ''}
                placeholder="Ej: alergias, sin picante..."
                onBlur={(e) => handleCustomerNote(e.target.value)}
              />

              <div className="mt-4 d-grid gap-2">
                {hasItems && (
                  <Button variant="primary" onClick={() => setShowConfirm(true)} style={{ borderRadius: 999 }}>
                    <i className="bi bi-send me-1" />Enviar a cocina
                  </Button>
                )}
                <Button variant="outline-light" onClick={() => navigate('/menu')} style={{ borderRadius: 999 }}>
                  <i className="bi bi-arrow-left me-2" />Volver al menú
                </Button>
              </div>

              <div className="mt-3 p-3 rounded-3" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-color)' }}>
                <small style={{ opacity: 0.8 }}>* Tiempo estimado: 20-25 minutos.</small>
              </div>
            </div>

            <div className="p-4 rounded-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
              <h5 className="mb-2">¿Cómo funciona?</h5>
              <p className="mb-0" style={{ opacity: 0.85 }}>
                Añade platos desde el menú, ajusta cantidades y envía la comanda. En cocina aparecerá en el tablón.
              </p>
            </div>
          </Col>
        </Row>
      </Container>

      {/* Confirm modal */}
      <Modal show={showConfirm} onHide={() => setShowConfirm(false)} centered>
        <Modal.Header closeButton style={{ background: 'var(--surface-color)', border: '1px solid var(--border-color)' }}>
          <Modal.Title>Confirmar comanda</Modal.Title>
        </Modal.Header>
        <Modal.Body style={{ background: 'var(--surface-color)' }}>
          ¿Seguro que quieres enviar la comanda a cocina? No podrás editarla después.
        </Modal.Body>
        <Modal.Footer style={{ background: 'var(--surface-color)', border: '1px solid var(--border-color)' }}>
          <Button variant="outline-light" onClick={() => setShowConfirm(false)} style={{ borderRadius: 999 }}>
            Cancelar
          </Button>
          <Button variant="primary" onClick={handleSubmit} disabled={submitting} style={{ borderRadius: 999 }}>
            {submitting ? <Spinner animation="border" size="sm" /> : 'Sí, enviar'}
          </Button>
        </Modal.Footer>
      </Modal>
    </section>
  );
}
