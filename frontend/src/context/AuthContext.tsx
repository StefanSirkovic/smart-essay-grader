import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import api from '../api/axios';
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types';

interface AuthUser {
  userId: number;
  email: string;
  role: string;
}

interface AuthContextType {
  user: AuthUser | null;
  token: string | null;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });
  const [token, setToken] = useState<string | null>(() =>
    localStorage.getItem('token')
  );

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
    } else {
      localStorage.removeItem('user');
    }
  }, [user]);

  const handleAuth = (data: AuthResponse) => {
    setToken(data.token);
    setUser({ userId: data.userId, email: data.email, role: data.role });
  };

  const login = async (data: LoginRequest) => {
    const res = await api.post<AuthResponse>('/auth/login', data);
    handleAuth(res.data);
  };

  const register = async (data: RegisterRequest) => {
    const res = await api.post<AuthResponse>('/auth/register', data);
    handleAuth(res.data);
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider
      value={{ user, token, login, register, logout, isAuthenticated: !!token }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
