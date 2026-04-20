import { create } from 'zustand';
import * as authService from '../services/authService';
import * as userService from '../services/userService';
import type { User } from '../types/user';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  initialized: boolean;
  fetchUser: () => Promise<void>;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isAuthenticated: false,
  initialized: false,

  fetchUser: async () => {
    if (get().initialized) return;
    try {
      const { data } = await userService.getMe();
      set({ user: data, isAuthenticated: true, initialized: true });
    } catch {
      set({ user: null, isAuthenticated: false, initialized: true });
    }
  },

  login: async (username, password) => {
    await authService.login(username, password);
    set({ initialized: false });
    await get().fetchUser();
  },

  logout: async () => {
    await authService.logout();
    set({ user: null, isAuthenticated: false, initialized: true });
  },
}));

export default useAuthStore;
