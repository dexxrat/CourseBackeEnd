import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { gameService } from '../services/gameService';
import { orderService } from '../services/orderService';
import GameFormModal from './GameFormModal';

function AdminPanel() {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const navigate = useNavigate();
  const [games, setGames] = useState([]);
  const [orders, setOrders] = useState([]);
  const [activeTab, setActiveTab] = useState('games');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingGame, setEditingGame] = useState(null);
  const [saveLoading, setSaveLoading] = useState(false);

  useEffect(() => {
    console.log('AdminPanel: Mounting with auth state:', {
      user,
      isAuthenticated: isAuthenticated(),
      isAdmin: isAdmin()
    });

    if (!isAuthenticated()) {
      console.log('AdminPanel: Not authenticated, redirecting to login');
      navigate('/login');
      return;
    }

    if (!isAdmin()) {
      console.log('AdminPanel: Not admin, redirecting to home');
      navigate('/');
      return;
    }

    console.log('AdminPanel: Access granted, user is admin');
  }, [isAuthenticated, isAdmin, navigate, user]);

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      console.log('Loading data for tab:', activeTab);

      if (activeTab === 'games') {
        const gamesData = await gameService.getAllGames();
        console.log('Loaded games:', gamesData);
        setGames(gamesData);
      } else {
        const ordersData = await orderService.getAllOrders();
        console.log('Loaded orders:', ordersData);
        setOrders(ordersData);
      }
    } catch (error) {
      console.error('Error loading data:', error);
      setError('Ошибка загрузки данных. Проверьте подключение к серверу.');
    } finally {
      setLoading(false);
    }
  }, [activeTab]);

  useEffect(() => {
    if (isAuthenticated() && isAdmin()) {
      loadData();
    }
  }, [isAuthenticated, isAdmin, loadData]);

  const handleLogout = () => {
    console.log('AdminPanel: Logging out from admin panel');
    logout();
    navigate('/');
  };

  const handleCreateGame = () => {
    setEditingGame(null);
    setModalOpen(true);
  };

  const handleEditGame = (game) => {
    setEditingGame(game);
    setModalOpen(true);
  };

  const handleSaveGame = async (gameData) => {
    try {
      setSaveLoading(true);
      console.log('Saving game:', gameData);

      if (editingGame) {
        await gameService.updateGame(editingGame.id, gameData);
        alert('Игра успешно обновлена!');
      } else {
        await gameService.createGame(gameData);
        alert('Игра успешно создана!');
      }

      setModalOpen(false);
      setEditingGame(null);
      await loadData();
    } catch (error) {
      console.error('Error saving game:', error);
      alert('Ошибка при сохранении игры: ' + error.message);
    } finally {
      setSaveLoading(false);
    }
  };

  const handleDeleteGame = async (gameId, gameTitle) => {
    if (window.confirm(`Вы уверены, что хотите удалить игру "${gameTitle}"?`)) {
      try {
        await gameService.deleteGame(gameId);
        await loadData();
        alert('Игра успешно удалена');
      } catch (error) {
        console.error('Error deleting game:', error);
        alert('Ошибка при удалении игры');
      }
    }
  };

  const handleUpdateOrderStatus = async (orderId, status) => {
    try {
      await orderService.updateOrderStatus(orderId, status);
      await loadData();
      alert('Статус заказа обновлен');
    } catch (error) {
      console.error('Error updating order status:', error);
      alert('Ошибка при обновлении статуса заказа');
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED': return '#00ff88';
      case 'PROCESSING': return '#ffaa00';
      case 'PENDING': return '#ffaa00';
      case 'CANCELLED': return '#ff4444';
      default: return '#cccccc';
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'COMPLETED': return 'Завершен';
      case 'PROCESSING': return 'В обработке';
      case 'PENDING': return 'Ожидание';
      case 'CANCELLED': return 'Отменен';
      default: return status;
    }
  };

  if (!isAuthenticated() || !isAdmin()) {
    return (
      <div className="container">
        <div className="text-center" style={{ padding: '4rem 1rem' }}>
          <div className="card" style={{ maxWidth: '500px', margin: '0 auto', padding: '3rem 2rem' }}>
            <h3>Доступ запрещен</h3>
            <p style={{ color: '#cccccc' }}>У вас нет прав для доступа к этой странице</p>
            <button
              onClick={() => navigate('/')}
              className="btn mt-2"
            >
              На главную
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (loading && games.length === 0 && orders.length === 0) {
    return (
      <div className="container">
        <div className="text-center" style={{ padding: '4rem 1rem' }}>
          <div className="card" style={{ maxWidth: '500px', margin: '0 auto', padding: '3rem 2rem' }}>
            <h3>Загрузка панели администратора...</h3>
            <p style={{ color: '#cccccc' }}>Пожалуйста, подождите</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <div className="text-center mb-4">
        <h1>Панель администратора</h1>
        <p style={{ color: '#cccccc' }}>Управление играми и заказами</p>
        <p style={{ color: '#888', fontSize: '0.9rem' }}>
          Вошли как: <strong>{user?.username}</strong>
        </p>
      </div>

      <div className="card mb-4">
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          padding: '1rem'
        }}>
          <div>
            <button
              onClick={() => navigate('/')}
              className="btn btn-secondary"
            >
              На главную
            </button>
          </div>
          <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
            <span style={{ color: '#00ff88' }}>Администратор</span>
            <button
              onClick={handleLogout}
              className="btn btn-danger"
            >
              Выйти
            </button>
          </div>
        </div>
      </div>

      <div className="card mb-4" style={{ overflow: 'hidden' }}>
        <div className="admin-tabs" style={{
          display: 'flex',
          gap: '0',
          borderRadius: 'var(--border-radius)',
          overflow: 'hidden',
          background: '#2a2a2a'
        }}>
          <button
            className={`tab-button ${activeTab === 'games' ? 'active' : ''}`}
            onClick={() => setActiveTab('games')}
            style={{
              flex: 1,
              padding: '1.25rem 2rem',
              background: activeTab === 'games' ? 'var(--primary-color)' : 'transparent',
              border: 'none',
              color: activeTab === 'games' ? 'var(--background-dark)' : 'var(--text-light)',
              cursor: 'pointer',
              transition: 'all 0.3s ease',
              fontWeight: '600',
              fontSize: '1rem',
              borderRight: '1px solid rgba(255,255,255,0.1)',
              position: 'relative',
              overflow: 'hidden'
            }}
          >
            Управление играми
            {activeTab === 'games' && (
              <div style={{
                position: 'absolute',
                bottom: 0,
                left: '50%',
                transform: 'translateX(-50%)',
                width: '80%',
                height: '3px',
                background: 'var(--background-dark)',
                borderRadius: '2px'
              }}></div>
            )}
          </button>

          <button
            className={`tab-button ${activeTab === 'orders' ? 'active' : ''}`}
            onClick={() => setActiveTab('orders')}
            style={{
              flex: 1,
              padding: '1.25rem 2rem',
              background: activeTab === 'orders' ? 'var(--primary-color)' : 'transparent',
              border: 'none',
              color: activeTab === 'orders' ? 'var(--background-dark)' : 'var(--text-light)',
              cursor: 'pointer',
              transition: 'all 0.3s ease',
              fontWeight: '600',
              fontSize: '1rem',
              position: 'relative',
              overflow: 'hidden'
            }}
          >
            Управление заказами
            {activeTab === 'orders' && (
              <div style={{
                position: 'absolute',
                bottom: 0,
                left: '50%',
                transform: 'translateX(-50%)',
                width: '80%',
                height: '3px',
                background: 'var(--background-dark)',
                borderRadius: '2px'
              }}></div>
            )}
          </button>
        </div>
      </div>

      {error && (
        <div className="alert alert-danger mb-4">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span>{error}</span>
            <button onClick={loadData} className="btn btn-secondary" style={{ marginLeft: '1rem' }}>
              Повторить
            </button>
          </div>
        </div>
      )}

      {activeTab === 'games' ? (
        <div className="games-management">
          <div className="card mb-4">
            <div className="admin-actions" style={{ padding: '1.5rem' }}>
              <h3 className="mb-2">Управление каталогом игр</h3>
              <p style={{ color: '#cccccc' }}>Всего игр в каталоге: {games.length}</p>
              <button
                onClick={handleCreateGame}
                className="btn mt-2"
                style={{
                  background: 'linear-gradient(135deg, var(--primary-color), #00cc77)',
                  border: 'none',
                  padding: '0.75rem 1.5rem',
                  borderRadius: 'var(--border-radius)',
                  color: 'white',
                  fontWeight: '600',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease'
                }}
                onMouseOver={(e) => {
                  e.target.style.transform = 'translateY(-2px)';
                  e.target.style.boxShadow = '0 4px 15px rgba(0, 255, 136, 0.3)';
                }}
                onMouseOut={(e) => {
                  e.target.style.transform = 'translateY(0)';
                  e.target.style.boxShadow = 'none';
                }}
              >
                Добавить новую игру
              </button>
            </div>
          </div>

          {games.length === 0 ? (
            <div className="card text-center" style={{ padding: '3rem 2rem', marginTop: '2rem' }}>
              <h3 style={{ color: '#00ff88', marginBottom: '1rem' }}>Игры не найдены</h3>
              <p style={{ color: '#cccccc', marginBottom: '2rem' }}>
                В каталоге пока нет игр. Начните с добавления первой игры!
              </p>
              <button
                onClick={handleCreateGame}
                className="btn"
                style={{
                  background: 'linear-gradient(135deg, var(--primary-color), #00cc77)',
                  border: 'none',
                  padding: '1rem 2rem',
                  borderRadius: 'var(--border-radius)',
                  color: 'white',
                  fontWeight: '600',
                  fontSize: '1.1rem',
                  cursor: 'pointer'
                }}
              >
                Добавить первую игру
              </button>
            </div>
          ) : (
            <div className="games-list">
              {games.map(game => (
                <div key={game.id} className="card game-admin-card" style={{
                  marginBottom: '1.5rem',
                  border: '1px solid rgba(255,255,255,0.1)',
                  transition: 'all 0.3s ease',
                  background: 'linear-gradient(135deg, rgba(42,42,42,0.9), rgba(34,34,34,0.9))'
                }}
                onMouseOver={(e) => {
                  e.currentTarget.style.transform = 'translateY(-5px)';
                  e.currentTarget.style.boxShadow = '0 8px 25px rgba(0, 255, 136, 0.15)';
                }}
                onMouseOut={(e) => {
                  e.currentTarget.style.transform = 'translateY(0)';
                  e.currentTarget.style.boxShadow = 'none';
                }}
                >
                  <div className="game-admin-content" style={{
                    display: 'grid',
                    gridTemplateColumns: 'auto 1fr auto',
                    gap: '1.5rem',
                    alignItems: 'start',
                    padding: '1.5rem'
                  }}>
                    <img
                      src={game.imageUrl || '/default-game.jpg'}
                      alt={game.title}
                      style={{
                        width: '100px',
                        height: '100px',
                        objectFit: 'cover',
                        borderRadius: '12px',
                        border: '2px solid rgba(255,255,255,0.1)'
                      }}
                    />

                    <div className="game-admin-info">
                      <h4 style={{
                        color: '#00ff88',
                        marginBottom: '0.5rem',
                        fontSize: '1.3rem',
                        fontWeight: '600'
                      }}>
                        {game.title}
                      </h4>
                      <div className="game-admin-meta" style={{
                        display: 'flex',
                        gap: '0.75rem',
                        marginBottom: '0.75rem',
                        flexWrap: 'wrap'
                      }}>
                        <span style={{
                          padding: '0.4rem 0.8rem',
                          borderRadius: '20px',
                          background: 'linear-gradient(135deg, rgba(0, 255, 136, 0.15), rgba(0, 255, 136, 0.25))',
                          color: '#00ff88',
                          fontSize: '0.85rem',
                          fontWeight: '600',
                          border: '1px solid rgba(0, 255, 136, 0.3)'
                        }}>
                          ${game.price}
                          {game.discountPrice && (
                            <span style={{
                              textDecoration: 'line-through',
                              marginLeft: '0.5rem',
                              color: '#ff6b6b',
                              opacity: '0.8'
                            }}>
                              ${game.discountPrice}
                            </span>
                          )}
                        </span>
                        <span style={{
                          padding: '0.4rem 0.8rem',
                          borderRadius: '20px',
                          background: 'rgba(255, 255, 255, 0.1)',
                          color: '#cccccc',
                          fontSize: '0.85rem',
                          fontWeight: '500',
                          border: '1px solid rgba(255,255,255,0.1)'
                        }}>
                          Жанры: {game.genres?.join(', ') || 'Не указаны'}
                        </span>
                        {game.publisher && (
                          <span style={{
                            padding: '0.4rem 0.8rem',
                            borderRadius: '20px',
                            background: 'rgba(255, 255, 255, 0.1)',
                            color: '#cccccc',
                            fontSize: '0.85rem',
                            fontWeight: '500',
                            border: '1px solid rgba(255,255,255,0.1)'
                          }}>
                            Издатель: {game.publisher}
                          </span>
                        )}
                      </div>
                      {game.description && (
                        <p style={{
                          color: '#cccccc',
                          fontSize: '0.95rem',
                          lineHeight: '1.5',
                          margin: '0.5rem 0',
                          opacity: '0.9'
                        }}>
                          {game.description.substring(0, 120)}...
                        </p>
                      )}
                      <div style={{
                        marginTop: '0.75rem',
                        fontSize: '0.85rem',
                        color: '#888',
                        display: 'flex',
                        gap: '1rem',
                        flexWrap: 'wrap'
                      }}>
                        <span>Платформа: {game.platform}</span>
                        {game.developer && <span>Разработчик: {game.developer}</span>}
                        {game.releaseDate && <span>Дата выхода: {new Date(game.releaseDate).toLocaleDateString('ru-RU')}</span>}
                      </div>
                    </div>

                    <div className="game-admin-actions" style={{
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '0.75rem',
                      minWidth: '160px'
                    }}>
                      <button
                        className="btn btn-secondary"
                        onClick={() => handleEditGame(game)}
                        style={{
                          padding: '0.75rem 1rem',
                          borderRadius: '8px',
                          border: '1px solid rgba(255,255,255,0.2)',
                          background: 'rgba(255,255,255,0.05)',
                          color: 'var(--text-light)',
                          cursor: 'pointer',
                          transition: 'all 0.3s ease',
                          fontWeight: '500'
                        }}
                        onMouseOver={(e) => {
                          e.target.style.background = 'rgba(255,255,255,0.1)';
                          e.target.style.borderColor = 'rgba(255,255,255,0.3)';
                        }}
                        onMouseOut={(e) => {
                          e.target.style.background = 'rgba(255,255,255,0.05)';
                          e.target.style.borderColor = 'rgba(255,255,255,0.2)';
                        }}
                      >
                        Редактировать
                      </button>
                      <button
                        className="btn btn-danger"
                        onClick={() => handleDeleteGame(game.id, game.title)}
                        style={{
                          padding: '0.75rem 1rem',
                          borderRadius: '8px',
                          border: '1px solid rgba(255,68,68,0.3)',
                          background: 'rgba(255,68,68,0.1)',
                          color: '#ff4444',
                          cursor: 'pointer',
                          transition: 'all 0.3s ease',
                          fontWeight: '500'
                        }}
                        onMouseOver={(e) => {
                          e.target.style.background = 'rgba(255,68,68,0.2)';
                          e.target.style.borderColor = 'rgba(255,68,68,0.5)';
                        }}
                        onMouseOut={(e) => {
                          e.target.style.background = 'rgba(255,68,68,0.1)';
                          e.target.style.borderColor = 'rgba(255,68,68,0.3)';
                        }}
                      >
                        Удалить
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      ) : (
        <div className="orders-management">
          <div className="card mb-4">
            <div className="admin-actions" style={{ padding: '1.5rem' }}>
              <h3 className="mb-2">Управление заказами</h3>
              <p style={{ color: '#cccccc' }}>Всего заказов: {orders.length}</p>
            </div>
          </div>

          {orders.length === 0 ? (
            <div className="card text-center" style={{ padding: '3rem 2rem', marginTop: '2rem' }}>
              <h3 style={{ color: '#00ff88', marginBottom: '1rem' }}>Заказы не найдены</h3>
              <p style={{ color: '#cccccc' }}>
                Пока нет ни одного заказа. Заказы появятся здесь, когда пользователи начнут делать покупки.
              </p>
            </div>
          ) : (
            <div className="orders-admin-list">
              {orders.map(order => (
                <div key={order.id} className="card order-admin-card" style={{
                  marginBottom: '1.5rem',
                  border: '1px solid rgba(255,255,255,0.1)',
                  transition: 'all 0.3s ease',
                  background: 'linear-gradient(135deg, rgba(42,42,42,0.9), rgba(34,34,34,0.9))'
                }}
                onMouseOver={(e) => {
                  e.currentTarget.style.transform = 'translateY(-3px)';
                  e.currentTarget.style.boxShadow = '0 6px 20px rgba(0, 255, 136, 0.1)';
                }}
                onMouseOut={(e) => {
                  e.currentTarget.style.transform = 'translateY(0)';
                  e.currentTarget.style.boxShadow = 'none';
                }}
                >
                  <div className="order-admin-content" style={{
                    display: 'grid',
                    gridTemplateColumns: '1fr auto',
                    gap: '1.5rem',
                    alignItems: 'start',
                    padding: '1.5rem'
                  }}>
                    <div className="order-admin-info">
                      <div className="order-admin-header" style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        marginBottom: '1rem',
                        flexWrap: 'wrap',
                        gap: '0.5rem'
                      }}>
                        <h4 style={{
                          color: '#00ff88',
                          margin: 0,
                          fontSize: '1.3rem',
                          fontWeight: '600'
                        }}>
                          Заказ #{order.id}
                        </h4>
                        <span
                          style={{
                            padding: '0.5rem 1.25rem',
                            borderRadius: '25px',
                            color: 'white',
                            fontSize: '0.8rem',
                            fontWeight: 'bold',
                            textTransform: 'uppercase',
                            backgroundColor: getStatusColor(order.status),
                            border: `2px solid ${getStatusColor(order.status)}`,
                            boxShadow: `0 2px 10px ${getStatusColor(order.status)}33`
                          }}
                        >
                          {getStatusText(order.status)}
                        </span>
                      </div>

                      <div className="order-admin-details" style={{ marginBottom: '1rem' }}>
                        <p style={{ margin: '0.5rem 0', color: '#cccccc', fontSize: '1rem' }}>
                          <strong>Пользователь:</strong> {order.userName || 'Неизвестно'}
                        </p>
                        <p style={{ margin: '0.5rem 0', color: '#cccccc', fontSize: '1rem' }}>
                          <strong>Дата:</strong> {new Date(order.orderDate).toLocaleDateString('ru-RU', {
                            year: 'numeric',
                            month: 'long',
                            day: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                          })}
                        </p>
                        <p style={{ margin: '0.5rem 0', color: '#cccccc', fontSize: '1rem' }}>
                          <strong>Сумма:</strong> <span style={{ color: '#00ff88', fontWeight: 'bold', fontSize: '1.1rem' }}>${order.totalAmount}</span>
                        </p>
                      </div>

                      <div className="order-admin-items" style={{ marginBottom: '1rem' }}>
                        <strong style={{ color: '#00ff88', fontSize: '1.1rem' }}>Товары:</strong>
                        {order.items?.map((item, index) => (
                          <div key={index} style={{
                            padding: '0.75rem',
                            color: '#cccccc',
                            fontSize: '0.95rem',
                            borderBottom: '1px solid rgba(255,255,255,0.1)',
                            background: 'rgba(255,255,255,0.02)',
                            borderRadius: '6px',
                            marginTop: '0.5rem',
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center'
                          }}>
                            <div>
                              <strong>{item.gameTitle}</strong>
                              {item.platform && <span style={{ marginLeft: '0.5rem', color: '#888', fontSize: '0.85rem' }}>({item.platform})</span>}
                            </div>
                            <div style={{ textAlign: 'right' }}>
                              <div style={{ color: '#00ff88', fontWeight: 'bold' }}>
                                ${item.priceAtPurchase}
                              </div>
                              <div style={{ color: '#888', fontSize: '0.85rem' }}>
                                {item.quantity} × ${item.priceAtPurchase}
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>

                    <div className="order-admin-controls" style={{
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '0.75rem',
                      minWidth: '180px'
                    }}>
                      <label style={{
                        color: '#00ff88',
                        fontWeight: '600',
                        fontSize: '1rem',
                        marginBottom: '0.5rem'
                      }}>
                        Статус:
                      </label>
                      <select
                        value={order.status}
                        onChange={(e) => handleUpdateOrderStatus(order.id, e.target.value)}
                        style={{
                          padding: '0.75rem',
                          borderRadius: '8px',
                          background: '#2a2a2a',
                          color: 'var(--text-light)',
                          border: '2px solid rgba(255,255,255,0.2)',
                          fontSize: '0.95rem',
                          cursor: 'pointer',
                          transition: 'all 0.3s ease'
                        }}
                        onFocus={(e) => {
                          e.target.style.borderColor = 'var(--primary-color)';
                          e.target.style.background = '#333';
                        }}
                        onBlur={(e) => {
                          e.target.style.borderColor = 'rgba(255,255,255,0.2)';
                          e.target.style.background = '#2a2a2a';
                        }}
                      >
                        <option value="PENDING">Ожидание</option>
                        <option value="PROCESSING">Обработка</option>
                        <option value="COMPLETED">Завершен</option>
                        <option value="CANCELLED">Отменен</option>
                      </select>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      <GameFormModal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setEditingGame(null);
        }}
        onSave={handleSaveGame}
        game={editingGame}
        loading={saveLoading}
      />

      <style jsx>{`
        @media (max-width: 1024px) {
          .game-admin-content,
          .order-admin-content {
            grid-template-columns: 1fr !important;
            gap: 1rem !important;
            text-align: center;
          }

          .game-admin-actions,
          .order-admin-controls {
            flex-direction: row !important;
            justify-content: center;
            min-width: auto !important;
          }

          .admin-tabs {
            flex-direction: column;
          }

          .tab-button {
            border-right: none !important;
            border-bottom: 1px solid rgba(255,255,255,0.1);
          }
        }

        @media (max-width: 768px) {
          .game-admin-meta {
            justify-content: center;
          }

          .order-admin-header {
            flex-direction: column;
            align-items: flex-start;
          }

          .game-admin-actions,
          .order-admin-controls {
            flex-direction: column !important;
          }

          .tab-button {
            padding: 1rem 1.5rem !important;
            font-size: 0.9rem !important;
          }
        }

        @media (max-width: 480px) {
          .game-admin-image {
            width: 80px !important;
            height: 80px !important;
          }

          .tab-button {
            padding: 0.875rem 1rem !important;
            font-size: 0.85rem !important;
          }
        }
      `}</style>
    </div>
  );
}

export default AdminPanel;