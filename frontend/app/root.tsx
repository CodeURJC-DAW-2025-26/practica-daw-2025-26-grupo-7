import { useEffect } from "react";
import {
  isRouteErrorResponse,
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
} from "react-router";

import type { Route } from "./+types/root";
import bootstrapCssUrl from 'bootstrap/dist/css/bootstrap.min.css?url';
import bootstrapIconsUrl from 'bootstrap-icons/font/bootstrap-icons.css?url';
import GlobalSpinner from "./components/GlobalSpinner";
import AppLayout from "./components/Layout";
import useAuthStore from "./stores/authStore";

export const links: Route.LinksFunction = () => [
  // Bootstrap must come before main.css so our theme overrides it correctly
  { rel: "stylesheet", href: bootstrapCssUrl },
  { rel: "stylesheet", href: bootstrapIconsUrl },
  { rel: "preconnect", href: "https://fonts.googleapis.com" },
  { rel: "preconnect", href: "https://fonts.gstatic.com", crossOrigin: "anonymous" },
  {
    rel: "stylesheet",
    href: "https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&family=Poppins:wght@300;400;500;600;700&family=Playfair+Display:wght@400;500;600;700;800&display=swap",
  },
  { rel: "stylesheet", href: "/new/css/main.css" },
  { rel: "icon", href: "/new/img/logo.png" },
];

export function Layout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="es">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <Meta />
        <Links />
      </head>
      <body>
        {children}
        <GlobalSpinner />
        <ScrollRestoration />
        <Scripts />
      </body>
    </html>
  );
}

export default function App() {
  useEffect(() => {
    useAuthStore.getState().fetchUser();
  }, []);

  return (
    <AppLayout>
      <Outlet />
    </AppLayout>
  );
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  const status = isRouteErrorResponse(error) ? error.status : 500;
  const title = status === 404 ? 'Página no encontrada'
    : status === 403 ? 'Acceso denegado'
    : 'Error inesperado';
  const message = status === 404 ? 'La página que buscas no existe o ha sido movida.'
    : status === 403 ? 'No tienes permisos para acceder a este recurso.'
    : import.meta.env.DEV && error instanceof Error ? error.message : 'Ha ocurrido un error. Inténtalo de nuevo.';

  return (
    <AppLayout>
      <section className="section" style={{ paddingTop: '120px', paddingBottom: '90px' }}>
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-lg-9 col-xl-8">
              <div className="p-5 rounded-4 shadow-lg"
                style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid var(--border-color)', backdropFilter: 'blur(6px)', minHeight: 300 }}>
                <div className="d-flex align-items-center gap-4 mb-4">
                  <div className="rounded-circle d-flex align-items-center justify-content-center"
                    style={{ width: 64, height: 64, background: 'rgba(255,0,0,0.12)', border: '1px solid rgba(255,0,0,0.35)' }}>
                    <i className="bi bi-exclamation-triangle" style={{ fontSize: '1.8rem' }} />
                  </div>
                  <div>
                    <h2 className="mb-1 fw-bold" style={{ fontSize: '1.9rem' }}>{title}</h2>
                    <small style={{ opacity: 0.75, fontSize: '0.95rem' }}>Código {status}</small>
                  </div>
                </div>
                <p className="mb-5" style={{ opacity: 0.9, fontSize: '1.05rem' }}>{message}</p>
                <div className="d-flex gap-3 flex-wrap">
                  <a className="btn btn-fuego px-4 py-2" href="/new/" style={{ borderRadius: 999 }}>
                    <i className="bi bi-house-door me-1" /> Inicio
                  </a>
                  <a className="btn btn-outline-light px-4 py-2" href="/new/menu" style={{ borderRadius: 999 }}>
                    <i className="bi bi-arrow-right-circle me-1" /> Ver menú
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </AppLayout>
  );
}
