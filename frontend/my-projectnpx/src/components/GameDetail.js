import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { gameService } from '../services/gameService';

function GameDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart, isInCart } = useCart();
  const { isAuthenticated } = useAuth();
  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [addingToCart, setAddingToCart] = useState(false);
  const [imageError, setImageError] = useState(false);

  const loadGame = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const gameData = await gameService.getGameById(id);
      setGame(gameData);
    } catch (error) {
      console.error('Error loading game:', error);
      setError('Игра не найдена или произошла ошибка загрузки.');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    loadGame();
  }, [loadGame]);

  const handleAddToCart = async () => {
    if (!game) return;

    if (!isAuthenticated) {
      alert('Пожалуйста, войдите в систему для добавления в корзину');
      return;
    }

    setAddingToCart(true);
    try {
      await addToCart(game);
      alert('Игра добавлена в корзину!');
    } catch (error) {
      console.error('Error adding to cart:', error);
      alert(error.message || 'Ошибка при добавлении в корзину');
    } finally {
      setAddingToCart(false);
    }
  };

  const handleBack = () => {
    navigate(-1);
  };

  const handleImageError = () => {
    setImageError(true);
  };

  const isGameInCart = isInCart(game?.id);

  if (loading) {
    return (
      <div className="container">
        <div className="loading">
          <div className="text-center">
            <h3>Загрузка игры...</h3>
            <p style={{ color: '#cccccc' }}>Пожалуйста, подождите</p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !game) {
    return (
      <div className="container">
        <div className="text-center">
          <div className="card" style={{ maxWidth: '500px', margin: '2rem auto' }}>
            <h3 className="mb-2">Ошибка</h3>
            <p className="mb-3" style={{ color: '#cccccc' }}>
              {error || 'Игра не найдена'}
            </p>
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap' }}>
              <button onClick={handleBack} className="btn btn-secondary">
                Назад
              </button>
              <button onClick={loadGame} className="btn">
                Попробовать снова
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <button onClick={handleBack} className="btn btn-secondary mb-4">
        ← Назад к каталогу
      </button>

      <div className="game-detail-grid">
        <div className="game-detail-image-section">
          <div className="card" style={{ position: 'relative' }}>
            <img
              src={imageError ? '/default-game.jpg' : (game.imageUrl || '/default-game.jpg')}
              alt={game.title}
              className="game-detail-image"
              onError={handleImageError}
            />
            {game.discountPrice && game.discountPrice < game.price && (
              <div className="discount-badge-large">
                -{Math.round((1 - game.discountPrice / game.price) * 100)}%
              </div>
            )}
          </div>
        </div>

        <div className="game-detail-info-section">
          <div className="card">
            <h1 className="game-detail-title">{game.title}</h1>

            <p className="game-detail-description">
              {game.description || 'Описание отсутствует'}
            </p>

            <div className="game-detail-meta">
              {game.genre && (
                <div className="meta-item">
                  <strong>Жанр:</strong>
                  <span>{game.genre}</span>
                </div>
              )}

              {game.publisher && (
                <div className="meta-item">
                  <strong>Издатель:</strong>
                  <span>{game.publisher}</span>
                </div>
              )}

              {game.developer && (
                <div className="meta-item">
                  <strong>Разработчик:</strong>
                  <span>{game.developer}</span>
                </div>
              )}

              {game.releaseDate && (
                <div className="meta-item">
                  <strong>Дата выхода:</strong>
                  <span>{new Date(game.releaseDate).toLocaleDateString('ru-RU')}</span>
                </div>
              )}

              {game.platform && (
                <div className="meta-item">
                  <strong>Платформа:</strong>
                  <span>{game.platform}</span>
                </div>
              )}
            </div>

            <div className="game-detail-price-section">
              {game.discountPrice && game.discountPrice < game.price ? (
                <div className="discount-prices-large">
                  <span className="original-price-large">${game.price}</span>
                  <span className="current-price-large">${game.discountPrice}</span>
                </div>
              ) : (
                <span className="current-price-large">${game.price}</span>
              )}
            </div>

            <button
              onClick={handleAddToCart}
              disabled={addingToCart || !game || isGameInCart}
              className={`btn w-100 add-to-cart-btn-large ${isGameInCart ? 'in-cart' : ''}`}
            >
              {addingToCart ? 'Добавление...' : isGameInCart ? 'В корзине' : 'Добавить в корзину'}
            </button>

            {!isAuthenticated && (
              <p style={{ color: '#ff6b6b', textAlign: 'center', marginTop: '1rem' }}>
                Войдите в систему для добавления в корзину
              </p>
            )}
          </div>
        </div>
      </div>

      <style jsx="true">{`
        .game-detail-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 2rem;
          align-items: start;
        }

        .game-detail-image {
          width: 100%;
          height: 400px;
          object-fit: cover;
          border-radius: 8px;
        }

        .discount-badge-large {
          position: absolute;
          top: 15px;
          right: 15px;
          background: #ff4444;
          color: white;
          padding: 0.5rem 1rem;
          border-radius: 20px;
          font-size: 1rem;
          font-weight: bold;
        }

        .game-detail-title {
          font-size: 2rem;
          color: #00ff88;
          margin-bottom: 1rem;
          line-height: 1.2;
        }

        .game-detail-description {
          color: #cccccc;
          line-height: 1.6;
          margin-bottom: 2rem;
          font-size: 1.1rem;
        }

        .game-detail-meta {
          display: flex;
          flex-direction: column;
          gap: 1rem;
          margin-bottom: 2rem;
        }

        .meta-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 0.75rem 0;
          border-bottom: 1px solid #333;
        }

        .meta-item strong {
          color: #00ff88;
        }

        .meta-item span {
          color: #ffffff;
        }

        .game-detail-price-section {
          margin-bottom: 2rem;
          text-align: center;
        }

        .discount-prices-large {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 1rem;
          margin-bottom: 1rem;
        }

        .original-price-large {
          text-decoration: line-through;
          color: #888;
          font-size: 1.5rem;
        }

        .current-price-large {
          color: #00ff88;
          font-size: 2.5rem;
          font-weight: bold;
        }

        .add-to-cart-btn-large {
          padding: 1rem 2rem;
          font-size: 1.1rem;
          background: #00ff88;
          color: #000;
          border: none;
          font-weight: bold;
          transition: all 0.3s ease;
        }

        .add-to-cart-btn-large:hover:not(:disabled) {
          background: #00cc6a;
          transform: translateY(-2px);
        }

        .add-to-cart-btn-large:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }

        .add-to-cart-btn-large.in-cart {
          background: #666;
          color: #ccc;
        }

        @media (max-width: 1024px) {
          .game-detail-grid {
            gap: 1.5rem;
          }

          .game-detail-image {
            height: 350px;
          }
        }

        @media (max-width: 768px) {
          .game-detail-grid {
            grid-template-columns: 1fr;
            gap: 1rem;
          }

          .game-detail-image {
            height: 300px;
          }

          .game-detail-title {
            font-size: 1.5rem;
          }

          .current-price-large {
            font-size: 2rem;
          }
        }

        @media (max-width: 480px) {
          .game-detail-image {
            height: 250px;
          }

          .meta-item {
            flex-direction: column;
            align-items: flex-start;
            gap: 0.25rem;
          }

          .game-detail-title {
            font-size: 1.3rem;
          }

          .add-to-cart-btn-large {
            padding: 0.75rem 1rem;
            font-size: 1rem;
          }
        }
      `}</style>
    </div>
  );
}

export default GameDetail;
