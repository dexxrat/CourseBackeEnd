import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { orderService } from '../services/orderService';

function Cart() {
  const {
    cartItems,
    removeFromCart,
    updateQuantity,
    getTotalPrice,
    getTotalItems,
    clearCart,
    loadCartFromServer,
    loading: cartLoading
  } = useCart();
  const { isAuthenticated } = useAuth();
  const [checkoutLoading, setCheckoutLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    console.log('Cart component - Current items:', cartItems);
    console.log('Cart component - Total items count:', getTotalItems());
    console.log('Cart component - Total price:', getTotalPrice());
  }, [cartItems, getTotalItems, getTotalPrice]);

  const handleCheckout = async () => {
    console.log('=== CHECKOUT STARTED ===');
    console.log('User authenticated:', isAuthenticated());
    console.log('Cart items:', cartItems);

    if (!isAuthenticated()) {
      setError('Пожалуйста, войдите в систему для оформления заказа');
      return;
    }

    if (cartItems.length === 0) {
      setError('Корзина пуста. Добавьте товары перед оформлением заказа.');
      return;
    }

    setCheckoutLoading(true);
    setError('');
    setSuccess('');

    try {
      console.log('Calling orderService.checkout()...');
      const result = await orderService.checkout();
      console.log('Checkout successful, result:', result);

      // Очищаем корзину после успешного оформления
      await clearCart();

      setSuccess('Заказ успешно оформлен! Перенаправление на страницу заказов...');

      setTimeout(() => {
        navigate('/orders');
      }, 2000);

    } catch (error) {
      console.error('=== CHECKOUT ERROR ===', error);

      let errorMessage = 'Ошибка при оформлении заказа. Пожалуйста, попробуйте еще раз.';

      if (error.message?.includes('400')) {
        errorMessage = 'Некорректный запрос';
      } else if (error.message?.includes('401')) {
        errorMessage = 'Пожалуйста, войдите в систему для оформления заказа.';
      } else if (error.message?.includes('403')) {
        errorMessage = 'У вас нет прав для выполнения этого действия.';
      } else if (error.message) {
        errorMessage = error.message;
      }

      setError(errorMessage);

      // Перезагружаем корзину в случае ошибки
      await loadCartFromServer();

    } finally {
      console.log('=== CHECKOUT FINISHED ===');
      setCheckoutLoading(false);
    }
  };

  const handleRefreshCart = async () => {
    console.log('Refreshing cart...');
    try {
      await loadCartFromServer();
      setError('');
      setSuccess('Корзина обновлена');
      setTimeout(() => setSuccess(''), 3000);
    } catch (error) {
      console.error('Error refreshing cart:', error);
      setError('Ошибка при обновлении корзины');
    }
  };

  const isLoading = cartLoading || checkoutLoading;

  if (cartItems.length === 0 && !cartLoading) {
    return (
      <div className="container">
        <div className="text-center" style={{ padding: '4rem 1rem' }}>
          <div className="card" style={{
            maxWidth: '500px',
            margin: '0 auto',
            padding: '3rem 2rem'
          }}>
            <h2 className="mb-2">Корзина пуста</h2>
            <p className="mb-3" style={{ color: '#cccccc' }}>
              Добавьте игры из каталога, чтобы сделать заказ!
            </p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <Link to="/games" className="btn">
                Перейти к играм
              </Link>
              <button
                onClick={handleRefreshCart}
                className="btn btn-secondary"
                disabled={isLoading}
              >
                {isLoading ? 'Обновление...' : 'Обновить корзину'}
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <h1 className="text-center mb-4">Корзина покупок</h1>

      {error && (
        <div className="alert alert-danger" style={{ marginBottom: '1rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span>{error}</span>
            <button
              onClick={() => setError('')}
              style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', fontSize: '1.2rem' }}
            >
              ×
            </button>
          </div>
        </div>
      )}

      {success && (
        <div className="alert alert-success" style={{ marginBottom: '1rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span>{success}</span>
            <button
              onClick={() => setSuccess('')}
              style={{ background: 'none', border: 'none', color: 'inherit', cursor: 'pointer', fontSize: '1.2rem' }}
            >
              ×
            </button>
          </div>
        </div>
      )}

      {cartLoading ? (
        <div className="text-center">
          <p>Загрузка корзины...</p>
        </div>
      ) : (
        <>
          <div className="cart-items" style={{ marginBottom: '2rem' }}>
            {cartItems.map(item => (
              <div key={item.id} className="card mb-2">
                <div className="cart-item-grid" style={{
                  display: 'grid',
                  gridTemplateColumns: 'auto 1fr auto auto auto',
                  gap: '1rem',
                  alignItems: 'center',
                  padding: '1rem'
                }}>
                  <img
                    src={item.imageUrl || '/default-game.jpg'}
                    alt={item.title}
                    style={{
                      width: '80px',
                      height: '80px',
                      objectFit: 'cover',
                      borderRadius: 'var(--border-radius)'
                    }}
                    onError={(e) => {
                      e.target.src = '/default-game.jpg';
                    }}
                  />

                  <div>
                    <h3 style={{ marginBottom: '0.5rem', color: '#00ff88' }}>{item.title}</h3>
                    <p style={{ color: '#cccccc', fontSize: '0.9rem' }}>${item.price}</p>
                    {item.platform && (
                      <p style={{ color: '#888', fontSize: '0.8rem' }}>{item.platform}</p>
                    )}
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <button
                      onClick={() => updateQuantity(item.id, item.quantity - 1)}
                      disabled={item.quantity <= 1 || isLoading}
                      className="btn btn-secondary"
                      style={{ padding: '0.5rem', minWidth: '40px' }}
                    >
                      -
                    </button>
                    <span style={{
                      padding: '0.5rem 1rem',
                      background: '#2a2a2a',
                      borderRadius: 'var(--border-radius)',
                      minWidth: '50px',
                      textAlign: 'center'
                    }}>
                      {item.quantity}
                    </span>
                    <button
                      onClick={() => updateQuantity(item.id, item.quantity + 1)}
                      className="btn btn-secondary"
                      style={{ padding: '0.5rem', minWidth: '40px' }}
                      disabled={isLoading}
                    >
                      +
                    </button>
                  </div>

                  <div style={{ textAlign: 'center' }}>
                    <strong style={{ color: '#00ff88' }}>
                      ${(item.price * item.quantity).toFixed(2)}
                    </strong>
                  </div>

                  <button
                    onClick={() => removeFromCart(item.id)}
                    className="btn btn-danger"
                    style={{ padding: '0.5rem 1rem' }}
                    disabled={isLoading}
                  >
                    Удалить
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="card">
            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              flexWrap: 'wrap',
              gap: '1rem',
              padding: '1.5rem'
            }}>
              <div>
                <h3 style={{ color: '#00ff88' }}>
                  Итого: ${getTotalPrice().toFixed(2)}
                </h3>
                <p style={{ color: '#cccccc', fontSize: '0.9rem' }}>
                  {getTotalItems()} товара(ов)
                </p>
              </div>

              <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
                <button
                  onClick={handleRefreshCart}
                  disabled={isLoading}
                  className="btn btn-secondary"
                >
                  {isLoading ? 'Обновление...' : 'Обновить'}
                </button>

                <button
                  onClick={clearCart}
                  className="btn btn-danger"
                  disabled={isLoading}
                >
                  Очистить корзину
                </button>

                <button
                  onClick={handleCheckout}
                  disabled={isLoading || cartItems.length === 0 || !isAuthenticated()}
                  className="btn"
                  title={!isAuthenticated() ? 'Войдите в систему для оформления заказа' : ''}
                >
                  {checkoutLoading ? 'Оформление...' : 'Оформить заказ'}
                </button>
              </div>
            </div>
          </div>
        </>
      )}

      <style jsx="true">{`
        @media (max-width: 768px) {
          .cart-item-grid {
            grid-template-columns: 1fr !important;
            text-align: center;
            gap: 1rem;
          }

          .cart-item-grid > div {
            justify-content: center;
          }
        }

        @media (max-width: 480px) {
          .cart-item-grid {
            gap: 0.75rem;
          }
        }
      `}</style>
    </div>
  );
}

export default Cart;
