import React, { useState, useEffect } from 'react';
import { orderService } from '../services/orderService';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function OrderHistory() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (isAuthenticated()) {
      loadOrders();
    }
  }, [isAuthenticated]);

  const loadOrders = async () => {
    try {
      setLoading(true);
      setError('');
      console.log('OrderHistory: Loading orders...');

      const response = await orderService.getUserOrders();
      console.log('OrderHistory: Orders response:', response);

      // Убедимся, что response - это массив
      const ordersArray = Array.isArray(response) ? response : [];
      setOrders(ordersArray);

    } catch (error) {
      console.error('OrderHistory: Error loading orders:', error);
      setError(error.message || 'Ошибка при загрузке заказов');
      setOrders([]); // Устанавливаем пустой массив в случае ошибки
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('ru-RU', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (e) {
      return dateString;
    }
  };

  const getStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case 'COMPLETED':
        return '#00ff88';
      case 'PROCESSING':
        return '#ffaa00';
      case 'PENDING':
        return '#ffaa00';
      case 'CANCELLED':
        return '#ff4444';
      default:
        return '#cccccc';
    }
  };

  const getStatusText = (status) => {
    switch (status?.toUpperCase()) {
      case 'COMPLETED':
        return 'Завершен';
      case 'PROCESSING':
        return 'В обработке';
      case 'PENDING':
        return 'Ожидание';
      case 'CANCELLED':
        return 'Отменен';
      default:
        return status;
    }
  };

  if (!isAuthenticated()) {
    return (
      <div className="container">
        <div className="text-center" style={{ padding: '4rem 1rem' }}>
          <div className="card" style={{ maxWidth: '500px', margin: '0 auto', padding: '3rem 2rem' }}>
            <h2>Доступ запрещен</h2>
            <p style={{ color: '#cccccc' }}>Пожалуйста, войдите в систему для просмотра заказов.</p>
            <Link to="/login" className="btn mt-2">
              Войти в систему
            </Link>
          </div>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="container">
        <div className="text-center" style={{ padding: '4rem 1rem' }}>
          <div className="card" style={{ maxWidth: '500px', margin: '0 auto', padding: '3rem 2rem' }}>
            <h3>Загрузка заказов...</h3>
            <p style={{ color: '#cccccc' }}>Пожалуйста, подождите</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container">
        <div className="text-center">
          <div className="card" style={{ maxWidth: '500px', margin: '2rem auto', padding: '2rem' }}>
            <h3 className="mb-2">Ошибка</h3>
            <p className="mb-3" style={{ color: '#cccccc' }}>{error}</p>
            <button onClick={loadOrders} className="btn">
              Попробовать снова
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="text-center mb-4">
        <h1>История заказов</h1>
        <p style={{ color: '#cccccc' }}>Ваши покупки в ZenGame</p>
      </div>

      {orders.length === 0 && (
        <div className="text-center">
          <div className="card" style={{ maxWidth: '500px', margin: '2rem auto', padding: '3rem 2rem' }}>
            <h3 className="mb-2">Заказов пока нет</h3>
            <p className="mb-3" style={{ color: '#cccccc' }}>
              Начните покупки в нашем каталоге игр!
            </p>
            <Link to="/games" className="btn">
              Перейти к играм
            </Link>
          </div>
        </div>
      )}

      {orders.length > 0 && (
        <div className="orders-list">
          {orders.map(order => (
            <div key={order.id} className="card mb-4">
              <div className="card-header" style={{
                background: 'rgba(255,255,255,0.05)',
                borderBottom: '1px solid rgba(255,255,255,0.1)',
                padding: '1.5rem'
              }}>
                <div style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'flex-start',
                  flexWrap: 'wrap',
                  gap: '1rem'
                }}>
                  <div>
                    <h3 style={{ margin: 0, color: '#00ff88' }}>Заказ #{order.id}</h3>
                    <p style={{ margin: '0.5rem 0 0 0', color: '#cccccc', fontSize: '0.9rem' }}>
                      {formatDate(order.orderDate)}
                    </p>
                  </div>
                  <div style={{ textAlign: 'right' }}>
                    <span
                      style={{
                        backgroundColor: getStatusColor(order.status),
                        padding: '0.5rem 1rem',
                        borderRadius: '20px',
                        color: 'white',
                        fontSize: '0.8rem',
                        fontWeight: 'bold',
                        display: 'inline-block',
                        marginBottom: '0.5rem'
                      }}
                    >
                      {getStatusText(order.status)}
                    </span>
                    <div style={{ color: '#00ff88', fontWeight: 'bold', fontSize: '1.2rem' }}>
                      ${order.totalAmount?.toFixed(2) || '0.00'}
                    </div>
                  </div>
                </div>
              </div>

              <div className="card-body" style={{ padding: '1.5rem' }}>
                <h4 style={{ color: '#ffffff', marginBottom: '1rem' }}>Состав заказа:</h4>
                {order.items && order.items.length > 0 ? (
                  <div className="order-items">
                    {order.items.map((item, index) => (
                      <div key={index} style={{
                        display: 'flex',
                        alignItems: 'center',
                        padding: '1rem 0',
                        borderBottom: '1px solid rgba(255,255,255,0.1)',
                        gap: '1rem'
                      }}>
                        <img
                          src={item.imageUrl || '/default-game.jpg'}
                          alt={item.gameTitle}
                          style={{
                            width: '60px',
                            height: '60px',
                            objectFit: 'cover',
                            borderRadius: '8px'
                          }}
                        />
                        <div style={{ flex: 1 }}>
                          <h5 style={{ margin: '0 0 0.5rem 0', color: '#ffffff' }}>
                            {item.gameTitle}
                          </h5>
                          {item.platform && (
                            <p style={{ margin: 0, color: '#888', fontSize: '0.8rem' }}>
                              {item.platform}
                            </p>
                          )}
                        </div>
                        <div style={{ textAlign: 'right' }}>
                          <div style={{ color: '#00ff88', fontWeight: 'bold', fontSize: '1rem' }}>
                            ${item.priceAtPurchase?.toFixed(2) || '0.00'}
                          </div>
                          <div style={{ color: '#cccccc', fontSize: '0.9rem' }}>
                            {item.quantity} × ${item.priceAtPurchase?.toFixed(2) || '0.00'}
                          </div>
                          <div style={{ color: '#888', fontSize: '0.8rem', marginTop: '0.25rem' }}>
                            Итого: ${((item.quantity || 0) * (item.priceAtPurchase || 0)).toFixed(2)}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p style={{ color: '#888', textAlign: 'center', padding: '1rem' }}>
                    Нет информации о товарах
                  </p>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {orders.length > 0 && (
        <div className="text-center" style={{ marginTop: '2rem' }}>
          <button
            onClick={loadOrders}
            className="btn btn-secondary"
            disabled={loading}
          >
            {loading ? 'Обновление...' : 'Обновить список'}
          </button>
        </div>
      )}

      <style jsx>{`
        .orders-list {
          display: flex;
          flex-direction: column;
          gap: 1.5rem;
        }

        @media (max-width: 768px) {
          .card-header > div {
            flex-direction: column;
            align-items: flex-start;
          }

          .card-header > div > div:last-child {
            text-align: left;
            width: 100%;
          }
        }
      `}</style>
    </div>
  );
}

export default OrderHistory;
