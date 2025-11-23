import api from './api';

export const orderService = {
  async checkout() {
    try {
      console.log('Making checkout request to /api/orders');
      const response = await api.post('/api/orders');
      console.log('Checkout response:', response);

      // ВАЖНО: Ваш API возвращает данные напрямую, а не в response.data
      return response;
    } catch (error) {
      console.error('Checkout error:', error);

      // Обработка различных ошибок
      if (error.response?.status === 400) {
        const errorMessage = error.response?.data?.message || 'Некорректный запрос';
        if (errorMessage.includes('Cart is empty') || errorMessage.includes('корзина пуста')) {
          throw new Error('Корзина пуста. Добавьте товары перед оформлением заказа.');
        }
        throw new Error(errorMessage);
      }

      if (error.response?.status === 401) {
        throw new Error('Пожалуйста, войдите в систему для оформления заказа.');
      }

      if (error.response?.status === 403) {
        throw new Error('У вас нет прав для выполнения этого действия.');
      }

      throw new Error(error.response?.data?.message || 'Ошибка при оформлении заказа. Пожалуйста, попробуйте еще раз.');
    }
  },

  async getUserOrders() {
    try {
      const response = await api.get('/api/orders');
      console.log('User orders response:', response);

      // ВАЖНО: Ваш API возвращает данные напрямую
      const ordersData = response;

      // Убедимся, что возвращаем массив
      if (ordersData && Array.isArray(ordersData)) {
        console.log(`Found ${ordersData.length} orders`);
        return ordersData;
      } else {
        console.warn('Unexpected orders response format:', response);
        return [];
      }
    } catch (error) {
      console.error('Error fetching user orders:', error);
      if (error.response?.status === 401) {
        throw new Error('Пожалуйста, войдите в систему для просмотра заказов.');
      }
      throw error;
    }
  },

  async getOrder(orderId) {
    try {
      const response = await api.get(`/api/orders/${orderId}`);
      return response;
    } catch (error) {
      console.error('Error fetching order:', error);
      if (error.response?.status === 404) {
        throw new Error('Заказ не найден.');
      }
      throw error;
    }
  },

  async getAllOrders() {
    try {
      const response = await api.get('/api/orders/admin/all');
      return response;
    } catch (error) {
      console.error('Error fetching all orders:', error);
      if (error.response?.status === 403) {
        throw new Error('У вас нет прав администратора для просмотра всех заказов.');
      }
      throw error;
    }
  },

  async updateOrderStatus(orderId, status) {
    try {
      const response = await api.put(`/api/orders/${orderId}/status`, { status });
      return response;
    } catch (error) {
      console.error('Error updating order status:', error);
      if (error.response?.status === 403) {
        throw new Error('У вас нет прав для изменения статуса заказа.');
      }
      throw error;
    }
  }
};
