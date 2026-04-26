import { Outlet, redirect } from 'react-router';
import useAuthStore from '../stores/authStore';

export async function clientLoader() {
  await useAuthStore.getState().fetchUser();
  const { isAuthenticated, user } = useAuthStore.getState();
  if (!isAuthenticated) throw redirect('/login');
  if (!user?.roles.includes('ADMIN')) throw new Response('Forbidden', { status: 403 });
  return null;
}

export default function AdminGuard() {
  return <Outlet />;
}
