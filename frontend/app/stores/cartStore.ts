import { create } from 'zustand';

interface CartStore {
  itemCount: number;
  setItemCount: (count: number) => void;
  increment: () => void;
  reset: () => void;
}

const useCartStore = create<CartStore>((set) => ({
  itemCount: 0,
  setItemCount: (itemCount) => set({ itemCount }),
  increment: () => set((state) => ({ itemCount: state.itemCount + 1 })),
  reset: () => set({ itemCount: 0 }),
}));

export default useCartStore;
