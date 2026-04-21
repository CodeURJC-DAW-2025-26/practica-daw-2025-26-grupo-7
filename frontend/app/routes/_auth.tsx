import { Outlet, redirect } from 'react-router';
import useAuthStore from '../stores/authStore';

export async function clientLoader() {
  await useAuthStore.getState().fetchUser();
  if (!useAuthStore.getState().isAuthenticated) {
    throw redirect('/login');
  }
  return null;
}

export default function AuthGuard() {
  return <Outlet />;
}
