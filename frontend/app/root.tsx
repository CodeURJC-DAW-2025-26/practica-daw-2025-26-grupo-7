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
  let message = "Oops!";
  let details = "An unexpected error occurred.";
  let stack: string | undefined;

  if (isRouteErrorResponse(error)) {
    message = error.status === 404 ? "404" : "Error";
    details =
      error.status === 404
        ? "The requested page could not be found."
        : error.statusText || details;
  } else if (import.meta.env.DEV && error && error instanceof Error) {
    details = error.message;
    stack = error.stack;
  }

  return (
    <main className="container py-5">
      <h1>{message}</h1>
      <p>{details}</p>
      {stack && (
        <pre className="p-4 overflow-auto">
          <code>{stack}</code>
        </pre>
      )}
    </main>
  );
}
