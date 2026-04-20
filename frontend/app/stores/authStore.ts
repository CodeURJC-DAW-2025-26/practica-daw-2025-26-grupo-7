import { create } from 'zustand';
import * as authService from '../services/authService';
import * as userService from '../services/userService';
import type { User } from '../types/user';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  fetchUser: () => Promise<void>;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,

  fetchUser: async () => {
    try {
      const { data } = await userService.getMe();
      set({ user: data, isAuthenticated: true });
    } catch {
      set({ user: null, isAuthenticated: false });
    }
  },

  login: async (username, password) => {
    await authService.login(username, password);
    await useAuthStore.getState().fetchUser();
  },

  logout: async () => {
    await authService.logout();
    set({ user: null, isAuthenticated: false });
  },
}));

export default useAuthStore;
