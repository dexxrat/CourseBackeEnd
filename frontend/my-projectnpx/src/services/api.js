const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

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
