import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';

function Header() {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const { getTotalItems, cartItems } = useCart();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const handleLogout = () => {
    console.log('Header: Logging out...');
    logout();
    navigate('/');
    setIsMenuOpen(false);
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/games?search=${encodeURIComponent(searchQuery)}`);
      setIsMenuOpen(false);
    }
  };

  // Отладочная информация
  console.log('Header - Auth state:', {
    user,
    isAuthenticated: isAuthenticated(),
    isAdmin: isAdmin()
  });
  console.log('Header - Cart items count:', getTotalItems());

  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          <Link to="/" className="logo" onClick={() => setIsMenuOpen(false)}>
            ZenGame
          </Link>

          {/* Поисковая строка */}
          <form onSubmit={handleSearch} className="header-search">
            <input
              type="text"
              placeholder="Поиск игр..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="search-input"
            />
            <button type="submit" className="btn btn-secondary search-btn">
              Найти
            </button>
          </form>

          {/* Мобильное меню */}
          <button
            className="btn btn-secondary mobile-menu-btn"
            onClick={toggleMenu}
          >
            ☰
          </button>

          <nav className={`nav ${isMenuOpen ? 'nav-open' : ''}`}>
            <Link to="/games" onClick={() => setIsMenuOpen(false)}>Каталог</Link>
            <Link to="/cart" onClick={() => setIsMenuOpen(false)} className="cart-link">
              Корзина
              {getTotalItems() > 0 && (
                <span className="cart-badge">({getTotalItems()})</span>
              )}
            </Link>

            {isAuthenticated() ? (
              <div className="user-controls">
                <Link to="/orders" onClick={() => setIsMenuOpen(false)}>Мои заказы</Link>
                {isAdmin() && (
                  <Link to="/admin" onClick={() => setIsMenuOpen(false)}>Админ</Link>
                )}
                <span className="user-greeting">Привет, {user?.username}</span>
                <button
                  onClick={handleLogout}
                  className="btn btn-danger logout-btn"
                >
                  Выйти
                </button>
              </div>
            ) : (
              <div className="auth-links">
                <Link to="/login" onClick={() => setIsMenuOpen(false)}>Войти</Link>
                <Link to="/register" onClick={() => setIsMenuOpen(false)}>Регистрация</Link>
              </div>
            )}
          </nav>
        </div>
      </div>

      <style jsx>{`
        .header {
          background-color: var(--card-dark);
          border-bottom: 2px solid var(--primary-color);
          padding: 1rem 0;
          position: sticky;
          top: 0;
          z-index: 1000;
        }

        .header-content {
          display: grid;
          grid-template-columns: auto 1fr auto;
          gap: 2rem;
          align-items: center;
        }

        .logo {
          font-size: 1.5rem;
          font-weight: bold;
          color: var(--primary-color);
          text-decoration: none;
          transition: var(--transition);
        }

        .logo:hover {
          color: var(--primary-dark);
        }

        .header-search {
          display: flex;
          gap: 0.5rem;
          max-width: 400px;
          width: 100%;
        }

        .search-input {
          flex: 1;
          padding: 0.5rem 1rem;
          border: 2px solid #333;
          border-radius: var(--border-radius);
          background: #2a2a2a;
          color: var(--text-light);
          font-size: 0.9rem;
        }

        .search-input:focus {
          outline: none;
          border-color: var(--primary-color);
        }

        .search-btn {
          white-space: nowrap;
        }

        .mobile-menu-btn {
          display: none;
        }

        .nav {
          display: flex;
          gap: 1.5rem;
          align-items: center;
        }

        .nav a {
          color: var(--text-light);
          text-decoration: none;
          font-weight: 500;
          transition: var(--transition);
          padding: 0.5rem 0;
          position: relative;
        }

        .nav a:hover {
          color: var(--primary-color);
        }

        .cart-link {
          display: flex;
          align-items: center;
          gap: 0.25rem;
        }

        .cart-badge {
          background: var(--primary-color);
          color: white;
          border-radius: 50%;
          width: 20px;
          height: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 0.7rem;
          font-weight: bold;
        }

        .user-greeting {
          color: var(--primary-color);
          font-weight: 500;
        }

        .logout-btn {
          padding: 0.5rem 1rem;
        }

        .user-controls, .auth-links {
          display: flex;
          gap: 1rem;
          align-items: center;
        }

        /* Мобильные стили */
        @media (max-width: 1024px) {
          .header-content {
            grid-template-columns: auto 1fr auto;
            gap: 1rem;
          }

          .header-search {
            max-width: 300px;
          }
        }

        @media (max-width: 768px) {
          .header-content {
            grid-template-columns: auto auto;
            grid-template-areas:
              "logo menu-btn"
              "search search"
              "nav nav";
            gap: 1rem;
          }

          .logo {
            grid-area: logo;
          }

          .header-search {
            grid-area: search;
            max-width: none;
          }

          .mobile-menu-btn {
            grid-area: menu-btn;
            display: block;
            justify-self: end;
          }

          .nav {
            grid-area: nav;
            display: none;
            flex-direction: column;
            width: 100%;
            gap: 1rem;
            margin-top: 1rem;
            padding-top: 1rem;
            border-top: 1px solid #333;
          }

          .nav-open {
            display: flex !important;
          }

          .user-controls, .auth-links {
            flex-direction: column;
            width: 100%;
            gap: 0.5rem;
          }

          .user-controls > *, .auth-links > * {
            width: 100%;
            text-align: center;
          }
        }

        @media (max-width: 480px) {
          .header-search {
            flex-direction: column;
          }

          .search-btn {
            width: 100%;
          }
        }
      `}</style>
    </header>
  );
}

export default Header;

