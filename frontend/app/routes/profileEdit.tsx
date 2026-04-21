import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Container, Row, Col, Form, Button, Alert, Spinner } from 'react-bootstrap';
import type { Route } from './+types/profileEdit';
import * as userService from '../services/userService';
import useAuthStore from '../stores/authStore';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Editar perfil' }];
}

export default function ProfileEdit({}: Route.ComponentProps) {
  const navigate = useNavigate();
  const { user, fetchUser } = useAuthStore();

  const [email, setEmail] = useState(user?.email ?? '');
  const [birthDate, setBirthDate] = useState(user?.birthDate ?? '');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);
    setSubmitting(true);
    try {
      await userService.updateMe({ email, birthDate: birthDate || undefined });
      useAuthStore.setState({ initialized: false });
      await fetchUser();
      setSuccess(true);
      setTimeout(() => navigate('/profile'), 1200);
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Error al actualizar el perfil.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="section" style={{ paddingTop: '120px', paddingBottom: '60px' }}>
      <Container>
        <Row className="justify-content-center">
          <Col lg={6} md={8}>

            <div className="d-flex align-items-end justify-content-between flex-wrap gap-2 mb-4">
              <div>
                <h2 className="mb-1">Editar perfil</h2>
                <p className="mb-0" style={{ opacity: 0.7 }}>Actualiza tus datos personales</p>
              </div>
            </div>

            <div className="p-4 rounded-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
              {error && <Alert variant="danger" onClose={() => setError(null)} dismissible>{error}</Alert>}
              {success && <Alert variant="success">Perfil actualizado correctamente.</Alert>}

              <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                  <Form.Label>Usuario</Form.Label>
                  <Form.Control
                    type="text"
                    value={user?.username ?? ''}
                    disabled
                    style={{ opacity: 0.6 }}
                  />
                  <Form.Text style={{ opacity: 0.6 }}>El nombre de usuario no se puede cambiar.</Form.Text>
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Correo electrónico <span style={{ color: 'var(--accent-color)' }}>*</span></Form.Label>
                  <Form.Control
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    placeholder="tu@email.com"
                  />
                </Form.Group>

                <Form.Group className="mb-4">
                  <Form.Label>Fecha de nacimiento</Form.Label>
                  <Form.Control
                    type="date"
                    value={birthDate}
                    onChange={(e) => setBirthDate(e.target.value)}
                  />
                </Form.Group>

                <div className="d-flex gap-3 flex-wrap">
                  <Button
                    type="submit"
                    variant="primary"
                    disabled={submitting}
                    style={{ borderRadius: 999 }}
                  >
                    {submitting ? <Spinner animation="border" size="sm" /> : <><i className="bi bi-check2 me-2" />Guardar cambios</>}
                  </Button>
                  <Button
                    variant="outline-light"
                    onClick={() => navigate('/profile')}
                    style={{ borderRadius: 999 }}
                    disabled={submitting}
                  >
                    <i className="bi bi-arrow-left me-2" />Cancelar
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
