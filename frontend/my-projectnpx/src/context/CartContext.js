import React, { createContext, useState, useContext, useEffect, useCallback } from 'react';
import { cartService } from '../services/cartService';
import { useAuth } from './AuthContext';

const CartContext = createContext();

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within CartProvider');
  }
  return context;
};

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const { user, isAuthenticated } = useAuth();

  // Загружаем корзину с сервера
  const loadCartFromServer = useCallback(async () => {
    if (!isAuthenticated || !isAuthenticated()) {
      console.log('User not authenticated, clearing cart');
      setCartItems([]);
      localStorage.removeItem('cart');
      return;
    }

    try {
      setLoading(true);
      console.log('Loading cart from server for user:', user?.id);
      const response = await cartService.getCart();
      console.log('Cart service response:', response);

      // ИСПРАВЛЕНИЕ: Правильное извлечение данных из response
      let cartData;
      if (response && response.data !== undefined) {
        cartData = response.data;
      } else {
        cartData = response;
      }

      console.log('Cart data:', cartData);

      if (cartData && Array.isArray(cartData.items)) {
        console.log('Found cart items:', cartData.items.length);
        const serverCartItems = cartData.items.map(item => ({
          id: item.id,
          gameId: item.gameId,
          title: item.gameTitle || 'Unknown Game',
          price: parseFloat(item.price) || 0,
          imageUrl: item.imageUrl || '/default-game.jpg',
          platform: item.platform || '',
          quantity: parseInt(item.quantity) || 1
        }));
        console.log('Processed cart items:', serverCartItems);
        setCartItems(serverCartItems);

        // Сохраняем в localStorage как резервную копию
        localStorage.setItem('cart', JSON.stringify(serverCartItems));
      } else {
        console.log('No items in cart or invalid format');
        setCartItems([]);
        localStorage.removeItem('cart');
      }
    } catch (error) {
      console.error('Error loading cart from server:', error);
      // Fallback: используем локальную корзину
      const savedCart = localStorage.getItem('cart');
      if (savedCart) {
        try {
          const parsedCart = JSON.parse(savedCart);
          setCartItems(parsedCart);
        } catch (e) {
          console.error('Error parsing saved cart:', e);
          setCartItems([]);
        }
      }
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, user]);

  // Загружаем корзину при монтировании и при изменении пользователя
  useEffect(() => {
    loadCartFromServer();
  }, [loadCartFromServer]);

  const addToCart = async (game) => {
    console.log('Adding to cart:', game);

    try {
      if (!isAuthenticated || !isAuthenticated()) {
        throw new Error('Пожалуйста, войдите в систему для добавления в корзину');
      }

      if (!game || !game.id) {
        throw new Error('Invalid game data');
      }

      console.log('Adding to server cart...');
      const response = await cartService.addToCart(game.id, 1);
      console.log('Add to cart response:', response);

      // Перезагружаем корзину после добавления
      await loadCartFromServer();

      return { success: true, message: 'Игра добавлена в корзину!' };

    } catch (error) {
      console.error('Error adding to cart:', error);

      if (error.response?.status === 401) {
        throw new Error('Пожалуйста, войдите в систему для добавления товаров в корзину');
      }

      throw error;
    }
  };

  const updateQuantity = async (itemId, newQuantity) => {
    try {
      if (newQuantity <= 0) {
        await removeFromCart(itemId);
        return;
      }

      const cartItem = cartItems.find(item => item.id === itemId);
      if (!cartItem) {
        throw new Error('Item not found in cart');
      }

      // Обновляем на сервере
      if (isAuthenticated && isAuthenticated()) {
        await cartService.updateCartItem(cartItem.id, newQuantity);
      }

      // Обновляем локальное состояние
      setCartItems(prevItems =>
        prevItems.map(item =>
          item.id === itemId ? { ...item, quantity: newQuantity } : item
        )
      );

    } catch (error) {
      console.error('Error updating cart item:', error);
      throw error;
    }
  };

  const removeFromCart = async (itemId) => {
    try {
      const cartItem = cartItems.find(item => item.id === itemId);
      if (!cartItem) {
        throw new Error('Item not found in cart');
      }

      // Удаляем с сервера
      if (isAuthenticated && isAuthenticated()) {
        await cartService.removeCartItem(cartItem.id);
      }

      // Удаляем из локального состояния
      setCartItems(prevItems => prevItems.filter(item => item.id !== itemId));

    } catch (error) {
      console.error('Error removing from cart:', error);
      throw error;
    }
  };

  const clearCart = async () => {
    try {
      // Очищаем на сервере
      if (isAuthenticated && isAuthenticated()) {
        await cartService.clearCart();
      }
      // Очищаем локально
      setCartItems([]);
      localStorage.removeItem('cart');
    } catch (error) {
      console.error('Error clearing cart:', error);
      setCartItems([]);
      localStorage.removeItem('cart');
    }
  };

  const getTotalPrice = () => {
    return cartItems.reduce((total, item) => {
      const itemTotal = (item.price || 0) * (item.quantity || 0);
      return total + itemTotal;
    }, 0);
  };

  const getTotalItems = () => {
    return cartItems.reduce((total, item) => total + (item.quantity || 0), 0);
  };

  const isInCart = (gameId) => {
    return cartItems.some(item => item.gameId === gameId);
  };

  const value = {
    cartItems,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    getTotalPrice,
    getTotalItems,
    isInCart,
    loadCartFromServer,
    loading
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
};const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = {
  async request(endpoint, options = {}) {
    const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;

    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    };

    // Добавляем токен авторизации если есть
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    try {
      console.log(`Making ${config.method || 'GET'} request to: ${url}`);
      if (config.body) {
        console.log('Request body:', config.body);
      }

      const response = await fetch(url, config);

      // Обработка 401 Unauthorized
      if (response.status === 401) {
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        window.location.href = '/login';
        throw new Error('Unauthorized');
      }

      // Проверка на успешный ответ
      if (!response.ok) {
        let errorMessage = `HTTP error! status: ${response.status}`;
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorMessage;
          console.error('API error response:', errorData);
        } catch (e) {
          // Ignore JSON parsing error
          console.error('Error parsing error response:', e);
        }
        throw new Error(errorMessage);
      }

      // Проверка на пустой ответ
      const contentLength = response.headers.get('Content-Length');
      const contentType = response.headers.get('Content-Type');

      if (contentLength === '0' || response.status === 204) {
        console.log('Empty response, returning null');
        return null;
      }

      if (!contentType?.includes('application/json')) {
        console.log('Non-JSON response, returning text');
        return await response.text();
      }

      const data = await response.json();
      console.log('API response data:', data);
      return data;

    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  },

  get(endpoint) {
    return this.request(endpoint);
  },

  post(endpoint, data) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },

  put(endpoint, data) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },

  delete(endpoint) {
    return this.request(endpoint, {
      method: 'DELETE',
    });
  },
};

export default api;
