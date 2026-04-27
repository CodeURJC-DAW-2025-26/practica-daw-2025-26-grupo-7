import { useState } from 'react';
import { Link, useNavigate } from 'react-router';
import type { Route } from './+types/home';
import useAuthStore from '../stores/authStore';
import * as userService from '../services/userService';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Acceso' }];
}

export default function Login() {
  const navigate = useNavigate();
  const { login } = useAuthStore();

  const [activeTab, setActiveTab] = useState<'login' | 'register'>('login');

  const [loginData, setLoginData] = useState({ username: '', password: '' });
  const [loginError, setLoginError] = useState('');
  const [loginLoading, setLoginLoading] = useState(false);

  const [registerData, setRegisterData] = useState({ username: '', email: '', password: '', password2: '', birthDate: '' });
  const [registerError, setRegisterError] = useState('');
  const [registerLoading, setRegisterLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoginError('');
    setLoginLoading(true);
    try {
      await login(loginData.username, loginData.password);
      navigate('/');
    } catch {
      setLoginError('Usuario o contraseña incorrectos.');
    } finally {
      setLoginLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setRegisterError('');
    if (registerData.password !== registerData.password2) {
      setRegisterError('Las contraseñas no coinciden.');
      return;
    }
    setRegisterLoading(true);
    try {
      await userService.register(registerData.username, registerData.email, registerData.password, registerData.birthDate);
      await login(registerData.username, registerData.password);
      navigate('/');
    } catch {
      setRegisterError('No se pudo completar el registro. El usuario o email ya existe.');
    } finally {
      setRegisterLoading(false);
    }
  };

  return (
    <section className="login-section section d-flex align-items-center" style={{ minHeight: '100vh' }}>
      <div className="container">
        <div className="login-card">
          <div className="login-card-header">
            <Link to="/" className="login-back">
              <i className="bi bi-arrow-left" /> Volver al inicio
            </Link>
            <div className="login-brand">
              <img src={`${import.meta.env.BASE_URL}img/logo.png`} alt="Fuego Lento" />
              <div>
                <h2>Pide desde tu mesa</h2>
                <p>Inicia sesión y envía tus comandas directamente a cocina.</p>
              </div>
            </div>
          </div>

          <div className="login-tabs">
            <button
              className={`login-tab${activeTab === 'login' ? ' active' : ''}`}
              type="button"
              onClick={() => setActiveTab('login')}
            >
              Iniciar sesión
            </button>
            <button
              className={`login-tab${activeTab === 'register' ? ' active' : ''}`}
              type="button"
              onClick={() => setActiveTab('register')}
            >
              Crear cuenta
            </button>
          </div>

          <div className="login-panels">

            {/* LOGIN */}
            <div className={`login-panel${activeTab === 'login' ? ' active' : ''}`}>
              <form onSubmit={handleLogin}>
                <div className="row g-3">
                  {loginError && (
                    <div className="col-12">
                      <div className="alert alert-danger mb-0">{loginError}</div>
                    </div>
                  )}
                  <div className="col-12">
                    <label className="form-label" htmlFor="loginUsername">Usuario</label>
                    <div className="input-with-icon">
                      <i className="bi bi-person" />
                      <input
                        id="loginUsername" type="text" className="form-control"
                        placeholder="tu usuario" required
                        value={loginData.username}
                        onChange={(e) => setLoginData({ ...loginData, username: e.target.value })}
                      />
                    </div>
                  </div>
                  <div className="col-12">
                    <label className="form-label" htmlFor="loginPassword">Contraseña</label>
                    <div className="input-with-icon">
                      <i className="bi bi-lock" />
                      <input
                        id="loginPassword" type="password" className="form-control"
                        placeholder="••••••••" required
                        value={loginData.password}
                        onChange={(e) => setLoginData({ ...loginData, password: e.target.value })}
                      />
                    </div>
                  </div>
                  <div className="col-12">
                    <button type="submit" className="btn btn-primary w-100 btn-fuego" disabled={loginLoading}>
                      {loginLoading ? 'Entrando...' : 'Entrar'}
                    </button>
                  </div>
                </div>
              </form>
            </div>

            {/* REGISTER */}
            <div className={`login-panel${activeTab === 'register' ? ' active' : ''}`}>
              <form onSubmit={handleRegister}>
                <div className="row g-3">
                  {registerError && (
                    <div className="col-12">
                      <div className="alert alert-danger mb-0">{registerError}</div>
                    </div>
                  )}
                  <div className="col-12">
                    <label className="form-label" htmlFor="regName">Nombre de usuario</label>
                    <div className="input-with-icon">
                      <i className="bi bi-person" />
                      <input
                        id="regName" type="text" className="form-control"
                        placeholder="Tu usuario" required
                        value={registerData.username}
                        onChange={(e) => setRegisterData({ ...registerData, username: e.target.value })}
                      />
                    </div>
                  </div>
                  <div className="col-12">
                    <label className="form-label" htmlFor="regEmail">Email</label>
                    <div className="input-with-icon">
                      <i className="bi bi-envelope" />
                      <input
                        id="regEmail" type="email" className="form-control"
                        placeholder="tuemail@ejemplo.com" required
                        value={registerData.email}
                        onChange={(e) => setRegisterData({ ...registerData, email: e.target.value })}
                      />
                    </div>
                  </div>
                  <div className="col-md-6">
                    <label className="form-label" htmlFor="regPass">Contraseña</label>
                    <div className="input-with-icon">
                      <i className="bi bi-lock" />
                      <input
                        id="regPass" type="password" className="form-control"
                        placeholder="••••••••" required
                        value={registerData.password}
                        onChange={(e) => setRegisterData({ ...registerData, password: e.target.value })}
                      />
                    </div>
                  </div>
                  <div className="col-md-6">
                    <label className="form-label" htmlFor="regPass2">Confirmación</label>
                    <div className="input-with-icon">
                      <i className="bi bi-shield-lock" />
                      <input
                        id="regPass2" type="password" className="form-control"
                        placeholder="••••••••" required
                        value={registerData.password2}
                        onChange={(e) => setRegisterData({ ...registerData, password2: e.target.value })}
                      />
                    </div>
                  </div>
                  <div className="col-12">
                    <label className="form-label" htmlFor="regBirth">Fecha de nacimiento</label>
                    <div className="input-with-icon">
                      <i className="bi bi-calendar-event" />
                      <input
                        id="regBirth" type="date" className="form-control" required
                        value={registerData.birthDate}
                        onChange={(e) => setRegisterData({ ...registerData, birthDate: e.target.value })}
                      />
                    </div>
                  </div>
                  <div className="col-12">
                    <button type="submit" className="btn btn-primary w-100 btn-fuego" disabled={registerLoading}>
                      {registerLoading ? 'Registrando...' : 'Crear cuenta'}
                    </button>
                  </div>
                  <div className="col-12 text-center">
                    <p className="login-small">
                      Al registrarte aceptas el <a className="login-link" href="#">aviso legal</a> y la{' '}
                      <a className="login-link" href="#">política de privacidad</a>.
                    </p>
                  </div>
                </div>
              </form>
            </div>

          </div>
        </div>
      </div>
    </section>
  );
}
