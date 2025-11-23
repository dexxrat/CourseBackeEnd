import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      try {
        console.log('AuthContext: Initializing authentication...');
        const userData = authService.getCurrentUser();
        const isAuth = authService.isAuthenticated();

        console.log('AuthContext: User data from storage:', userData);
        console.log('AuthContext: Is authenticated:', isAuth);

        if (userData && isAuth) {
          setUser(userData);
          console.log('AuthContext: User authenticated successfully:', userData.username);
        } else {
          console.log('AuthContext: No valid authentication found');
          setUser(null);
          // Очищаем невалидные данные
          if (userData && !isAuth) {
            console.log('AuthContext: Cleaning up invalid auth data');
            authService.logout();
          }
        }
      } catch (error) {
        console.error('AuthContext: Error during auth initialization:', error);
        setUser(null);
        authService.logout();
      } finally {
        setLoading(false);
        console.log('AuthContext: Auth initialization completed');
      }
    };

    initAuth();
  }, []);

  const login = async (username, password) => {
    try {
      console.log('AuthContext: Attempting login...');
      const response = await authService.login(username, password);
      const userData = {
        id: response.id,
        username: response.username,
        email: response.email,
        roles: response.roles || ['ROLE_USER']
      };
      setUser(userData);
      console.log('AuthContext: Login successful for user:', userData.username);
      return response;
    } catch (error) {
      console.error('AuthContext: Login failed:', error);
      throw error;
    }
  };

  const logout = () => {
    console.log('AuthContext: Logging out...');
    authService.logout();
    setUser(null);
    console.log('AuthContext: Logout completed');
  };

  // Функции должны быть объявлены перед value
  const checkIsAdmin = () => {
    const result = user && user.roles && user.roles.includes('ROLE_ADMIN');
    console.log('AuthContext: isAdmin check:', result, 'User roles:', user?.roles);
    return result;
  };

  const checkIsAuthenticated = () => {
    const result = !!user && authService.isAuthenticated();
    console.log('AuthContext: isAuthenticated check:', result, 'User exists:', !!user);
    return result;
  };

  const value = {
    user,
    login,
    logout,
    isAdmin: checkIsAdmin, // Передаем функцию, а не результат
    isAuthenticated: checkIsAuthenticated, // Передаем функцию, а не результат
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

