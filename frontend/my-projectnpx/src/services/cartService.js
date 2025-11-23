import api from './api';

export const cartService = {
  async getCart() {
    try {
      console.log('cartService: Getting cart...');
      const response = await api.get('/api/cart');
      console.log('cartService: Full response:', response);

      // Axios автоматически помещает данные в response.data
      // Возвращаем весь response для корректной обработки в CartContext
      return response;
    } catch (error) {
      console.error('cartService: Error getting cart:', error);

      // Возвращаем структуру, совместимую с успешным ответом axios
      return {
        data: {
          id: 0,
          userId: 0,
          totalPrice: 0,
          items: []
        }
      };
    }
  },

  async addToCart(gameId, quantity = 1) {
    try {
      console.log('cartService: Adding to cart - gameId:', gameId, 'quantity:', quantity);

      if (!gameId) {
        throw new Error('Game ID is required');
      }

      const response = await api.post('/api/cart/items', {
        gameId: parseInt(gameId),
        quantity: parseInt(quantity)
      });

      console.log('cartService: Add to cart response:', response);
      return response; // Возвращаем весь response

    } catch (error) {
      console.error('cartService: Error adding to cart:', error);

      if (error.response?.status === 401) {
        throw new Error('Пожалуйста, войдите в систему');
      } else if (error.response?.status === 404) {
        throw new Error('Игра не найдена');
      } else if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      } else if (error.message) {
        throw new Error(error.message);
      } else {
        throw new Error('Ошибка при добавлении в корзину');
      }
    }
  },

  async updateCartItem(itemId, quantity) {
    try {
      console.log('cartService: Updating cart item - itemId:', itemId, 'quantity:', quantity);

      if (!itemId) {
        throw new Error('Item ID is required');
      }

      const response = await api.put(`/api/cart/items/${itemId}`, {
        quantity: parseInt(quantity)
      });

      console.log('cartService: Update cart item response:', response);
      return response; // Возвращаем весь response

    } catch (error) {
      console.error('cartService: Error updating cart item:', error);

      if (error.response?.status === 404) {
        throw new Error('Элемент корзины не найден');
      } else if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      } else {
        throw new Error('Ошибка при обновлении корзины');
      }
    }
  },

  async removeCartItem(itemId) {
    try {
      console.log('cartService: Removing cart item - itemId:', itemId);

      if (!itemId) {
        throw new Error('Item ID is required');
      }

      const response = await api.delete(`/api/cart/items/${itemId}`);
      console.log('cartService: Remove cart item response:', response);
      return response; // Возвращаем весь response

    } catch (error) {
      console.error('cartService: Error removing cart item:', error);

      if (error.response?.status === 404) {
        throw new Error('Элемент корзины не найден');
      } else {
        throw new Error('Ошибка при удалении из корзины');
      }
    }
  },

  async clearCart() {
    try {
      console.log('cartService: Clearing cart...');
      const response = await api.delete('/api/cart');
      console.log('cartService: Clear cart response:', response);
      return response; // Возвращаем весь response

    } catch (error) {
      console.error('cartService: Error clearing cart:', error);
      throw new Error('Ошибка при очистке корзины');
    }
  }
};

