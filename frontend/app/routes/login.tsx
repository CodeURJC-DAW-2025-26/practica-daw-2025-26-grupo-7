import { useState } from 'react';
import { useNavigate } from 'react-router';
import { Container, Row, Col, Card, Tab, Nav, Form, Button, Alert } from 'react-bootstrap';
import type { Route } from './+types/home';
import useAuthStore from '../stores/authStore';
import * as userService from '../services/userService';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Acceso' }];
}

export default function Login() {
  const navigate = useNavigate();
  const { login } = useAuthStore();

  const [loginData, setLoginData] = useState({ username: '', password: '' });
  const [loginError, setLoginError] = useState('');
  const [loginLoading, setLoginLoading] = useState(false);

  const [registerData, setRegisterData] = useState({ username: '', email: '', password: '', birthDate: '' });
  const [registerError, setRegisterError] = useState('');
  const [registerLoading, setRegisterLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoginError('');
    setLoginLoading(true);
    try {
      await login(loginData.username, loginData.password);
      navigate('/new/');
    } catch {
      setLoginError('Usuario o contraseña incorrectos.');
    } finally {
      setLoginLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setRegisterError('');
    setRegisterLoading(true);
    try {
      await userService.register(
        registerData.username,
        registerData.email,
        registerData.password,
        registerData.birthDate,
      );
      await login(registerData.username, registerData.password);
      navigate('/new/');
    } catch {
      setRegisterError('No se pudo completar el registro. El usuario o email ya existe.');
    } finally {
      setRegisterLoading(false);
    }
  };

  return (
    <section className="section dark-background" style={{ minHeight: '80vh', display: 'flex', alignItems: 'center' }}>
      <Container>
        <Row className="justify-content-center">
          <Col md={8} lg={5}>
            <div className="section-title text-center mb-4">
              <h2>Acceso</h2>
              <p>Inicia sesión o crea tu cuenta</p>
            </div>
            <Card style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.10)' }}>
              <Card.Body className="p-4">
                <Tab.Container defaultActiveKey="login">
                  <Nav variant="tabs" className="mb-4">
                    <Nav.Item>
                      <Nav.Link eventKey="login">Iniciar sesión</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                      <Nav.Link eventKey="register">Registrarse</Nav.Link>
                    </Nav.Item>
                  </Nav>

                  <Tab.Content>
                    <Tab.Pane eventKey="login">
                      {loginError && <Alert variant="danger" className="py-2">{loginError}</Alert>}
                      <Form onSubmit={handleLogin}>
                        <Form.Group className="mb-3">
                          <Form.Label>Usuario</Form.Label>
                          <Form.Control
                            type="text"
                            placeholder="Nombre de usuario"
                            value={loginData.username}
                            onChange={(e) => setLoginData({ ...loginData, username: e.target.value })}
                            required
                          />
                        </Form.Group>
                        <Form.Group className="mb-4">
                          <Form.Label>Contraseña</Form.Label>
                          <Form.Control
                            type="password"
                            placeholder="Contraseña"
                            value={loginData.password}
                            onChange={(e) => setLoginData({ ...loginData, password: e.target.value })}
                            required
                          />
                        </Form.Group>
                        <div className="text-center">
                          <Button type="submit" variant="" className="cta-btn" disabled={loginLoading}>
                            {loginLoading ? 'Entrando...' : 'Iniciar sesión'}
                          </Button>
                        </div>
                      </Form>
                    </Tab.Pane>

                    <Tab.Pane eventKey="register">
                      {registerError && <Alert variant="danger" className="py-2">{registerError}</Alert>}
                      <Form onSubmit={handleRegister}>
                        <Form.Group className="mb-3">
                          <Form.Label>Usuario</Form.Label>
                          <Form.Control
                            type="text"
                            placeholder="Nombre de usuario"
                            value={registerData.username}
                            onChange={(e) => setRegisterData({ ...registerData, username: e.target.value })}
                            required
                          />
                        </Form.Group>
                        <Form.Group className="mb-3">
                          <Form.Label>Email</Form.Label>
                          <Form.Control
                            type="email"
                            placeholder="correo@ejemplo.com"
                            value={registerData.email}
                            onChange={(e) => setRegisterData({ ...registerData, email: e.target.value })}
                            required
                          />
                        </Form.Group>
                        <Form.Group className="mb-3">
                          <Form.Label>Contraseña</Form.Label>
                          <Form.Control
                            type="password"
                            placeholder="Contraseña"
                            value={registerData.password}
                            onChange={(e) => setRegisterData({ ...registerData, password: e.target.value })}
                            required
                          />
                        </Form.Group>
                        <Form.Group className="mb-4">
                          <Form.Label>Fecha de nacimiento</Form.Label>
                          <Form.Control
                            type="date"
                            value={registerData.birthDate}
                            onChange={(e) => setRegisterData({ ...registerData, birthDate: e.target.value })}
                            required
                          />
                        </Form.Group>
                        <div className="text-center">
                          <Button type="submit" variant="" className="cta-btn" disabled={registerLoading}>
                            {registerLoading ? 'Registrando...' : 'Crear cuenta'}
                          </Button>
                        </div>
                      </Form>
                    </Tab.Pane>
                  </Tab.Content>
                </Tab.Container>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </section>
  );
}
