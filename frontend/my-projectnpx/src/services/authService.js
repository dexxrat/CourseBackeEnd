import api from './api';

export const authService = {
  async login(username, password) {
    try {
      console.log('authService: Attempting login for user:', username);
      const response = await api.post('/api/auth/login', {
        username,
        password
      });

      console.log('authService: Login response:', response);

      if (response && response.token) {
        localStorage.setItem('authToken', response.token);

        const userData = {
          id: response.id,
          username: response.username,
          email: response.email,
          roles: response.roles || ['ROLE_USER'] // Роль по умолчанию
        };

        localStorage.setItem('user', JSON.stringify(userData));
        console.log('authService: Login successful for user:', response.username);
        return { ...response, ...userData };
      } else {
        throw new Error('No token received from server');
      }
    } catch (error) {
      console.error('authService: Login failed:', error);

      let userMessage = 'Ошибка входа';
      if (error.message?.includes('Invalid credentials') || error.message?.includes('Неверные учетные данные')) {
        userMessage = 'Неверное имя пользователя или пароль';
      } else if (error.message?.includes('400')) {
        userMessage = 'Неверные данные для входа';
      } else if (error.message?.includes('401')) {
        userMessage = 'Неверные учетные данные';
      } else if (error.message) {
        userMessage = error.message;
      }

      throw new Error(userMessage);
    }
  },

  async register(userData) {
    try {
      console.log('authService: Attempting registration for user:', userData.username);

      // Check username availability
      try {
        await api.get(`/api/auth/check-username/${userData.username}`);
      } catch (error) {
        if (error.message?.includes('409')) {
          throw new Error('Имя пользователя уже занято');
        }
      }

      // Check email availability
      try {
        await api.get(`/api/auth/check-email/${userData.email}`);
      } catch (error) {
        if (error.message?.includes('409')) {
          throw new Error('Email уже используется');
        }
      }

      // Register user
      const response = await api.post('/api/auth/register', {
        username: userData.username,
        email: userData.email,
        password: userData.password
      });

      console.log('authService: Registration response:', response);

      if (response && response.token) {
        localStorage.setItem('authToken', response.token);

        const newUserData = {
          id: response.id,
          username: response.username,
          email: response.email,
          roles: response.roles || ['ROLE_USER']
        };

        localStorage.setItem('user', JSON.stringify(newUserData));
        console.log('authService: Registration successful for user:', response.username);
        return { ...response, ...newUserData };
      } else {
        throw new Error('Ошибка регистрации - токен не получен');
      }
    } catch (error) {
      console.error('authService: Registration failed:', error);
      throw error;
    }
  },

  logout() {
    console.log('authService: Logging out user');
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    console.log('authService: User logged out successfully');
  },

  getCurrentUser() {
    try {
      const userStr = localStorage.getItem('user');
      if (!userStr) {
        console.log('authService: No user data found in storage');
        return null;
      }

      const userData = JSON.parse(userStr);
      console.log('authService: Retrieved user data:', userData);
      return userData;
    } catch (error) {
      console.error('authService: Error parsing user data:', error);
      this.logout();
      return null;
    }
  },

  getToken() {
    const token = localStorage.getItem('authToken');
    console.log('authService: Token exists:', !!token);
    return token;
  },

  isAuthenticated() {
    const token = this.getToken();
    if (!token) {
      console.log('authService: No token found');
      return false;
    }

    try {
      // Проверяем структуру токена
      const parts = token.split('.');
      if (parts.length !== 3) {
        console.log('authService: Invalid token structure');
        this.logout();
        return false;
      }

      const payload = JSON.parse(atob(parts[1]));
      const isExpired = payload.exp * 1000 < Date.now();

      console.log('authService: Token expiration check:', {
        expires: new Date(payload.exp * 1000),
        now: new Date(),
        isExpired
      });

      if (isExpired) {
        console.log('authService: Token expired');
        this.logout();
        return false;
      }

      console.log('authService: Token is valid');
      return true;
    } catch (error) {
      console.error('authService: Error checking token:', error);
      this.logout();
      return false;
    }
  },

  isAdmin() {
    const user = this.getCurrentUser();
    const result = user && user.roles && user.roles.includes('ROLE_ADMIN');
    console.log('authService: isAdmin check:', result);
    return result;
  }
};
