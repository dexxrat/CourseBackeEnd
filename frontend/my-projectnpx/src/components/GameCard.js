import React, { useState } from 'react';
import { useCart } from '../context/CartContext';
import { useNavigate } from 'react-router-dom';

function GameCard({ game }) {
  const { addToCart, isInCart } = useCart();
  const navigate = useNavigate();
  const [imageError, setImageError] = useState(false);
  const [addingToCart, setAddingToCart] = useState(false);

  const handleAddToCart = async (e) => {
    e.stopPropagation();

    if (addingToCart) return;

    setAddingToCart(true);
    try {
      await addToCart(game);
      // Можно показать уведомление об успешном добавлении
    } catch (error) {
      console.error('Error adding to cart:', error);
      alert(error.message || 'Ошибка при добавлении в корзину');
    } finally {
      setAddingToCart(false);
    }
  };

  const handleCardClick = () => {
    navigate(`/games/${game.id}`);
  };

  const handleImageError = () => {
    setImageError(true);
  };

  const isGameInCart = isInCart(game.id);

  // ИСПРАВЛЕНИЕ: Безопасное получение жанра
  const getGameGenre = () => {
    if (game.genre) return game.genre;
    if (game.genres && Array.isArray(game.genres) && game.genres.length > 0) {
      return game.genres[0];
    }
    return 'Жанр не указан';
  };

  return (
    <div className="card game-card" onClick={handleCardClick}>
      <div className="game-card-image">
        <img
          src={imageError ? '/default-game.jpg' : (game.imageUrl || '/default-game.jpg')}
          alt={game.title}
          className="game-image"
          onError={handleImageError}
        />
        {game.discountPrice && game.discountPrice < game.price && (
          <div className="discount-badge">
            -{Math.round((1 - game.discountPrice / game.price) * 100)}%
          </div>
        )}
      </div>

      <div className="game-card-content">
        <h3 className="game-title">{game.title}</h3>

        <p className="game-description">
          {game.description ? `${game.description.substring(0, 100)}...` : 'Описание отсутствует'}
        </p>

        <div className="game-meta">
          <span className="game-genre">{getGameGenre()}</span>
          {game.platform && (
            <span className="game-platform">{game.platform}</span>
          )}
        </div>

        <div className="game-price-section">
          {game.discountPrice && game.discountPrice < game.price ? (
            <div className="discount-prices">
              <span className="original-price">${game.price}</span>
              <span className="current-price">${game.discountPrice}</span>
            </div>
          ) : (
            <span className="current-price">${game.price}</span>
          )}
        </div>

        <button
          onClick={handleAddToCart}
          className={`btn w-100 add-to-cart-btn ${isGameInCart ? 'in-cart' : ''}`}
          disabled={addingToCart}
        >
          {addingToCart ? 'Добавление...' : isGameInCart ? 'В корзине' : 'В корзину'}
        </button>
      </div>

      <style jsx="true">{`
        .game-card {
          cursor: pointer;
          transition: all 0.3s ease;
          height: 100%;
          display: flex;
          flex-direction: column;
          border: 1px solid #333;
          background: #1a1a1a;
        }

        .game-card:hover {
          transform: translateY(-8px);
          box-shadow: 0 12px 40px rgba(0, 255, 136, 0.15);
          border-color: #00ff88;
        }

        .game-card-image {
          position: relative;
          overflow: hidden;
          border-radius: 8px 8px 0 0;
        }

        .game-image {
          width: 100%;
          height: 200px;
          object-fit: cover;
          transition: transform 0.3s ease;
        }

        .game-card:hover .game-image {
          transform: scale(1.05);
        }

        .discount-badge {
          position: absolute;
          top: 10px;
          right: 10px;
          background: #ff4444;
          color: white;
          padding: 0.25rem 0.5rem;
          border-radius: 12px;
          font-size: 0.8rem;
          font-weight: bold;
        }

        .game-card-content {
          padding: 1.5rem;
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 0.75rem;
        }

        .game-title {
          font-size: 1.2rem;
          color: #00ff88;
          line-height: 1.3;
          margin: 0;
          min-height: 3rem;
        }

        .game-description {
          color: #cccccc;
          font-size: 0.9rem;
          line-height: 1.4;
          margin: 0;
          flex: 1;
        }

        .game-meta {
          display: flex;
          gap: 0.5rem;
          flex-wrap: wrap;
        }

        .game-genre, .game-platform {
          background: rgba(0, 255, 136, 0.1);
          color: #00ff88;
          padding: 0.25rem 0.5rem;
          border-radius: 12px;
          font-size: 0.8rem;
          font-weight: 500;
        }

        .game-platform {
          background: rgba(255, 255, 255, 0.1);
          color: #cccccc;
        }

        .game-price-section {
          display: flex;
          align-items: center;
          margin-top: auto;
        }

        .discount-prices {
          display: flex;
          align-items: center;
          gap: 0.5rem;
        }

        .original-price {
          text-decoration: line-through;
          color: #888;
          font-size: 0.9rem;
        }

        .current-price {
          color: #00ff88;
          font-size: 1.3rem;
          font-weight: bold;
        }

        .add-to-cart-btn {
          margin-top: 1rem;
          background: #00ff88;
          color: #000;
          border: none;
          padding: 0.75rem;
          font-weight: bold;
          transition: all 0.3s ease;
        }

        .add-to-cart-btn:hover:not(:disabled) {
          background: #00cc6a;
          transform: translateY(-2px);
        }

        .add-to-cart-btn:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }

        .add-to-cart-btn.in-cart {
          background: #666;
          color: #ccc;
        }

        /* Адаптивность */
        @media (max-width: 768px) {
          .game-card-content {
            padding: 1rem;
            gap: 0.5rem;
          }

          .game-title {
            font-size: 1.1rem;
          }

          .game-image {
            height: 180px;
          }

          .current-price {
            font-size: 1.2rem;
          }
        }

        @media (max-width: 480px) {
          .game-image {
            height: 160px;
          }

          .game-card-content {
            padding: 0.75rem;
          }

          .game-title {
            font-size: 1rem;
          }

          .game-description {
            font-size: 0.85rem;
          }
        }
      `}</style>
    </div>
  );
}

export default GameCard;
