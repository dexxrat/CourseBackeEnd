import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

function Login() {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!formData.username.trim()) {
      setError('Пожалуйста, введите имя пользователя');
      return;
    }

    if (!formData.password) {
      setError('Пожалуйста, введите пароль');
      return;
    }

    setLoading(true);

    try {
      console.log('Login: Attempting login...');
      await login(formData.username, formData.password);
      console.log('Login: Success, redirecting to home');
      navigate('/');
    } catch (error) {
      console.error('Login: Error:', error);
      setError(error.message || 'Неверное имя пользователя или пароль');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="auth-container">
        <div className="auth-card">
          <h2 className="text-center mb-3">Вход в систему</h2>

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="username">Имя пользователя</label>
              <input
                type="text"
                id="username"
                name="username"
                className="form-input"
                placeholder="Введите ваше имя пользователя"
                value={formData.username}
                onChange={handleChange}
                disabled={loading}
                autoComplete="username"
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Пароль</label>
              <input
                type="password"
                id="password"
                name="password"
                className="form-input"
                placeholder="Введите ваш пароль"
                value={formData.password}
                onChange={handleChange}
                disabled={loading}
                autoComplete="current-password"
              />
            </div>

            {error && (
              <div className="alert alert-danger">
                {error}
              </div>
            )}

            <button
              type="submit"
              className="btn btn-primary w-100"
              disabled={loading || !formData.username || !formData.password}
            >
              {loading ? 'Вход...' : 'Войти'}
            </button>

            <div className="auth-links text-center mt-3">
              <p>
                Нет аккаунта?{' '}
                <Link to="/register" className="auth-link">
                  Зарегистрируйтесь здесь
                </Link>
              </p>
            </div>
          </form>
        </div>
      </div>

      <style jsx>{`
        .auth-container {
          display: flex;
          justify-content: center;
          align-items: center;
          min-height: 70vh;
          padding: 2rem 1rem;
        }

        .auth-card {
          background: var(--card-dark);
          border-radius: var(--border-radius);
          padding: 2.5rem;
          width: 100%;
          max-width: 400px;
          border: 1px solid rgba(255, 255, 255, 0.1);
          box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
        }

        .auth-form {
          display: flex;
          flex-direction: column;
          gap: 1.5rem;
        }

        .form-group {
          display: flex;
          flex-direction: column;
          gap: 0.5rem;
        }

        .form-group label {
          color: var(--text-light);
          font-weight: 500;
          font-size: 0.9rem;
        }

        .form-input {
          padding: 0.75rem 1rem;
          border: 2px solid #333;
          border-radius: var(--border-radius);
          background: #2a2a2a;
          color: var(--text-light);
          font-size: 1rem;
          transition: all 0.3s ease;
        }

        .form-input:focus {
          outline: none;
          border-color: var(--primary-color);
          background: #333;
        }

        .form-input:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }

        .auth-links {
          color: var(--text-muted);
          font-size: 0.9rem;
        }

        .auth-link {
          color: var(--primary-color);
          text-decoration: none;
          font-weight: 500;
        }

        .auth-link:hover {
          color: var(--primary-dark);
          text-decoration: underline;
        }

        .w-100 {
          width: 100%;
        }

        .text-center {
          text-align: center;
        }

        .mt-3 {
          margin-top: 1rem;
        }

        .mb-3 {
          margin-bottom: 1rem;
        }

        @media (max-width: 480px) {
          .auth-card {
            padding: 2rem 1.5rem;
          }
        }
      `}</style>
    </div>
  );
}

export default Login;

