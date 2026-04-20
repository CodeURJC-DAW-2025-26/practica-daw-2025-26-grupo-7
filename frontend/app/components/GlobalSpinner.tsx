import { Spinner } from 'react-bootstrap';
import useLoadingStore from '../stores/loadingStore';

export default function GlobalSpinner() {
  const isLoading = useLoadingStore((s) => s.isLoading);

  if (!isLoading) return null;

  return (
    <div style={{
      position: 'fixed',
      inset: 0,
      backgroundColor: 'rgba(0,0,0,0.5)',
      zIndex: 9999,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
    }}>
      <Spinner animation="border" variant="light" />
    </div>
  );
}
