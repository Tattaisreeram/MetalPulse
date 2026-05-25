import { createContext, useContext, useState, useCallback, type ReactNode } from 'react';
import type { AuthResponse } from '../types';

interface AuthContextValue {
  user: AuthResponse | null;
  login: (user: AuthResponse) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

function loadUser(): AuthResponse | null {
  try {
    const raw = localStorage.getItem('mp_user');
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthResponse | null>(loadUser);

  const login = useCallback((u: AuthResponse) => {
    localStorage.setItem('mp_token', u.token);
    localStorage.setItem('mp_user', JSON.stringify(u));
    setUser(u);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('mp_token');
    localStorage.removeItem('mp_user');
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be inside AuthProvider');
  return ctx;
}
