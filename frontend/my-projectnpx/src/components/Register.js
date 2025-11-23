import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/authService';

function Register() {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const validateForm = () => {
    if (formData.username.length < 3) {
      return 'Имя пользователя должно быть не менее 3 символов';
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      return 'Пожалуйста, введите корректный email адрес';
    }
    if (formData.password.length < 6) {
      return 'Пароль должен быть не менее 6 символов';
    }
    if (formData.password !== formData.confirmPassword) {
      return 'Пароли не совпадают';
    }
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);

    try {
      console.log('Register: Attempting registration...');
      await authService.register({
        username: formData.username,
        email: formData.email,
        password: formData.password
      });

      console.log('Register: Success, redirecting to login');
      navigate('/login', {
        state: { message: 'Регистрация успешна! Пожалуйста, войдите в систему.' }
      });
    } catch (error) {
      console.error('Register: Error:', error);
      setError(error.message || 'Ошибка регистрации. Пожалуйста, попробуйте еще раз.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="auth-container">
        <div className="auth-card">
          <h2 className="text-center mb-3">Регистрация</h2>

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="username">Имя пользователя</label>
              <input
                type="text"
                id="username"
                name="username"
                className="form-input"
                placeholder="Выберите имя пользователя"
                value={formData.username}
                onChange={handleChange}
                disabled={loading}
                minLength="3"
                autoComplete="username"
              />
              <div className="form-hint">
                Минимум 3 символа. Только буквы, цифры и подчеркивания.
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="email">Email адрес</label>
              <input
                type="email"
                id="email"
                name="email"
                className="form-input"
                placeholder="Введите ваш email"
                value={formData.email}
                onChange={handleChange}
                disabled={loading}
                autoComplete="email"
              />
              <div className="form-hint">
                Мы будем отправлять уведомления о заказах на этот email.
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="password">Пароль</label>
              <input
                type="password"
                id="password"
                name="password"
                className="form-input"
                placeholder="Придумайте пароль"
                value={formData.password}
                onChange={handleChange}
                disabled={loading}
                minLength="6"
                autoComplete="new-password"
              />
              <div className="form-hint">
                Минимум 6 символов. Рекомендуем использовать буквы, цифры и специальные символы.
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">Подтверждение пароля</label>
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                className="form-input"
                placeholder="Подтвердите ваш пароль"
                value={formData.confirmPassword}
                onChange={handleChange}
                disabled={loading}
                minLength="6"
                autoComplete="new-password"
              />
              <div className="form-hint">
                Пожалуйста, введите ваш пароль еще раз для подтверждения.
              </div>
            </div>

            {error && (
              <div className="alert alert-danger">
                {error}
              </div>
            )}

            <button
              type="submit"
              className="btn btn-primary w-100"
              disabled={loading}
            >
              {loading ? 'Регистрация...' : 'Зарегистрироваться'}
            </button>

            <div className="auth-links text-center mt-3">
              <p>
                Уже есть аккаунт?{' '}
                <Link to="/login" className="auth-link">
                  Войдите здесь
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
          max-width: 450px;
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

        .form-hint {
          color: var(--text-muted);
          font-size: 0.8rem;
          line-height: 1.4;
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

export default Register;
