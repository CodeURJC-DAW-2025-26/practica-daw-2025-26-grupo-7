import { useState } from 'react';
import { Container, Row, Col, Badge, Button, Form, Spinner } from 'react-bootstrap';
import type { Route } from './+types/adminUsers';
import * as userService from '../services/userService';
import useLoadingStore from '../stores/loadingStore';
import type { User } from '../types/user';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Fuego Lento | Gestión de usuarios' }];
}

export async function clientLoader() {
  useLoadingStore.getState().setLoading(true);
  try {
    const { data } = await userService.getAllUsers();
    return data;
  } finally {
    useLoadingStore.getState().setLoading(false);
  }
}

export default function AdminUsers({ loaderData }: Route.ComponentProps) {
  const [users, setUsers] = useState<User[]>(loaderData ?? []);
  const [search, setSearch] = useState('');
  const [actionId, setActionId] = useState<number | null>(null);

  const filtered = users.filter((u) =>
    u.username.toLowerCase().includes(search.toLowerCase()) ||
    u.email.toLowerCase().includes(search.toLowerCase())
  );

  const handleBan = async (user: User) => {
    setActionId(user.id);
    try {
      if (user.banned) {
        await userService.unbanUser(user.id);
      } else {
        await userService.banUser(user.id);
      }
      setUsers((prev) =>
        prev.map((u) => (u.id === user.id ? { ...u, banned: !u.banned } : u))
      );
    } finally {
      setActionId(null);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('¿Eliminar este usuario permanentemente?')) return;
    setActionId(id);
    try {
      await userService.deleteUser(id);
      setUsers((prev) => prev.filter((u) => u.id !== id));
    } finally {
      setActionId(null);
    }
  };

  return (
    <section className="section" style={{ paddingTop: '120px', paddingBottom: '60px' }}>
      <Container>
        <div className="d-flex align-items-end justify-content-between flex-wrap gap-2 mb-4">
          <div>
            <h2 className="mb-1">Gestión de usuarios</h2>
            <p className="mb-0" style={{ opacity: 0.7 }}>{users.length} usuarios registrados</p>
          </div>
        </div>

        <div className="mb-4">
          <Form.Control
            type="search"
            placeholder="Buscar por usuario o email..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ maxWidth: 400 }}
          />
        </div>

        <Row className="g-3">
          {filtered.map((user) => (
            <Col key={user.id} xs={12}>
              <div className="p-3 rounded-4" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-color)' }}>
                <div className="d-flex flex-wrap align-items-center justify-content-between gap-3">
                  <div>
                    <div className="d-flex align-items-center gap-2 mb-1">
                      <span className="fw-bold">{user.username}</span>
                      {user.roles.includes('ROLE_ADMIN') && (
                        <Badge bg="warning" text="dark">Admin</Badge>
                      )}
                      {user.banned && <Badge bg="danger">Baneado</Badge>}
                    </div>
                    <div className="small" style={{ opacity: 0.7 }}>
                      <i className="bi bi-envelope me-1" />{user.email}
                      {user.createdAt && (
                        <span className="ms-3">
                          <i className="bi bi-calendar me-1" />
                          {new Date(user.createdAt).toLocaleDateString('es-ES')}
                        </span>
                      )}
                    </div>
                  </div>

                  <div className="d-flex gap-2">
                    <Button
                      variant={user.banned ? 'outline-success' : 'outline-warning'}
                      size="sm"
                      onClick={() => handleBan(user)}
                      disabled={actionId === user.id || user.roles.includes('ROLE_ADMIN')}
                      style={{ borderRadius: 999 }}
                    >
                      {actionId === user.id
                        ? <Spinner animation="border" size="sm" />
                        : user.banned ? <><i className="bi bi-unlock me-1" />Desbanear</> : <><i className="bi bi-ban me-1" />Banear</>}
                    </Button>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      onClick={() => handleDelete(user.id)}
                      disabled={actionId === user.id || user.roles.includes('ROLE_ADMIN')}
                      style={{ borderRadius: 999 }}
                    >
                      <i className="bi bi-trash3 me-1" />Eliminar
                    </Button>
                  </div>
                </div>
              </div>
            </Col>
          ))}
          {filtered.length === 0 && (
            <Col xs={12}>
              <p style={{ opacity: 0.75 }}>No se encontraron usuarios.</p>
            </Col>
          )}
        </Row>
      </Container>
    </section>
  );
}
