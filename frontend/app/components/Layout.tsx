import AppNavbar from './AppNavbar';
import AppFooter from './AppFooter';

interface LayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: LayoutProps) {
  return (
    <>
      <AppNavbar />
      <main className="main">
        {children}
      </main>
      <AppFooter />
    </>
  );
}
