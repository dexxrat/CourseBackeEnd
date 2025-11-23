import React, { useState, useEffect } from 'react';

function GameFormModal({ isOpen, onClose, onSave, game = null, loading = false }) {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    price: '',
    discountPrice: '',
    developer: '',
    publisher: '',
    releaseDate: '',
    platform: 'PC',
    imageUrl: '',
    genres: []
  });
  const [selectedGenre, setSelectedGenre] = useState('');

  // Жанры для выбора
  const availableGenres = [
    'ACTION', 'RPG', 'STRATEGY', 'ADVENTURE', 'SIMULATION', 'SPORTS',
    'RACING', 'HORROR', 'PUZZLE', 'FIGHTING', 'SHOOTER', 'INDIE'
  ];

  // Платформы для выбора
  const platforms = ['PC', 'PLAYSTATION', 'XBOX', 'NINTENDO', 'MOBILE'];

  // Инициализация формы при открытии или изменении игры
  useEffect(() => {
    if (game) {
      // Режим редактирования
      setFormData({
        title: game.title || '',
        description: game.description || '',
        price: game.price || '',
        discountPrice: game.discountPrice || '',
        developer: game.developer || '',
        publisher: game.publisher || '',
        releaseDate: game.releaseDate || '',
        platform: game.platform || 'PC',
        imageUrl: game.imageUrl || '',
        genres: game.genres || []
      });
    } else {
      // Режим создания
      setFormData({
        title: '',
        description: '',
        price: '',
        discountPrice: '',
        developer: '',
        publisher: '',
        releaseDate: '',
        platform: 'PC',
        imageUrl: '',
        genres: []
      });
    }
    setSelectedGenre('');
  }, [game, isOpen]);

  const handleAddGenre = () => {
    if (selectedGenre && !formData.genres.includes(selectedGenre)) {
      setFormData(prev => ({
        ...prev,
        genres: [...prev.genres, selectedGenre]
      }));
      setSelectedGenre('');
    }
  };

  const handleRemoveGenre = (genreToRemove) => {
    setFormData(prev => ({
      ...prev,
      genres: prev.genres.filter(genre => genre !== genreToRemove)
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Валидация
    if (!formData.title.trim()) {
      alert('Пожалуйста, введите название игры');
      return;
    }
    if (!formData.price || parseFloat(formData.price) <= 0) {
      alert('Пожалуйста, введите корректную цену');
      return;
    }
    if (formData.genres.length === 0) {
      alert('Пожалуйста, добавьте хотя бы один жанр');
      return;
    }

    // Подготовка данных
    const submitData = {
      ...formData,
      price: parseFloat(formData.price),
      discountPrice: formData.discountPrice ? parseFloat(formData.discountPrice) : null,
      genres: formData.genres
    };

    console.log('Submitting game data:', submitData);
    onSave(submitData);
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000
    }}>
      <div className="modal-content" style={{
        background: 'var(--card-dark)',
        borderRadius: 'var(--border-radius)',
        padding: '2rem',
        width: '90%',
        maxWidth: '600px',
        maxHeight: '90vh',
        overflowY: 'auto',
        border: '1px solid rgba(255, 255, 255, 0.1)'
      }}>
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '1.5rem'
        }}>
          <h2 style={{ color: '#00ff88', margin: 0 }}>
            {game ? 'Редактировать игру' : 'Добавить новую игру'}
          </h2>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              color: '#cccccc',
              fontSize: '1.5rem',
              cursor: 'pointer'
            }}
          >
            ×
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {/* Основная информация */}
          <div className="form-section" style={{ marginBottom: '1.5rem' }}>
            <h3 style={{ color: '#00ff88', marginBottom: '1rem' }}>Основная информация</h3>
            
            <div className="form-group" style={{ marginBottom: '1rem' }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', color: '#cccccc' }}>
                Название игры *
              </label>
              <input
                type="text"
                value={formData.title}
                onChange={(e) => setFormData({...formData, title: e.target.value})}
                required
                style={{
                  width: '100%',
                  padding: '0.75rem',
                  border: '2px solid #333',
                  borderRadius: 'var(--border-radius)',
                  background: '#2a2a2a',
                  color: 'var(--text-light)'
                }}
                placeholder="Введите название игры"
              />
            </div>

            <div className="form-group" style={{ marginBottom: '1rem' }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', color: '#cccccc' }}>
                Описание *
              </label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({...formData, description: e.target.value})}
                required
                rows="4"
                style={{
                  width: '100%',
                  padding: '0.75rem',
                  border: '2px solid #333',
                  borderRadius: 'var(--border-radius)',
                  background: '#2a2a2a',
                  color: 'var(--text-light)',
                  resize: 'vertical'
                }}
                placeholder="Опишите игру"
              />
            </div>
          </div>

          {/* Цены */}
          <div className="form-section" style={{ marginBottom: '1.5rem' }}>
            <h3 style={{ color: '#00ff88', marginBottom: '1rem' }}>Цены</h3>
            
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label style={{ display: 'block', marginBottom: '0.5rem', color: '#cccccc' }}>
                  Цена ($) *
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  value={formData.price}
                  onChange={(e) => setFormData({...formData, price: e.target.value})}
                  required
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '2px solid #333',
                    borderRadius: 'var(--border-radius)',
                    background: '#2a2a2a',
                    color: 'var(--text-light)'
                  }}
                  placeholder="0.00"
                />
              </div>

              <div className="form-group">
                <label style={{ display: 'block', marginBottom: '0.5rem', color: '#cccccc' }}>
                  Цена со скидкой ($)
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  value={formData.discountPrice}
                  onChange={(e) => setFormData({...formData, discountPrice: e.target.value})}
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '2px solid #333',
                    borderRadius: 'var(--border-radius)',
                    background: '#2a2a2a',
                    color: 'var(--text-light)'
                  }}
                  placeholder="0.00 (опционально)"
                />
              </div>
            </div>
          </div>

          {/* Детали */}
          <div className="form-section" style={{ marginBottom: '1.5rem' }}>
            <h3 style={{ color: '#00ff88', marginBottom: '1rem' }}>Детали</h3>
            
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
              <div className="form-group">
                <label style={{ display: 'block', marginBottom: '0.5rem', color: '#cccccc' }}>
                  Разработчик
                </label>
                <input
                  type="text"
                  value={formData.developer}
                  onChange={(e) => setFormData({...formData, developer: e.target.value})}
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '2px solid #333',
                    borderRadius: 'var(--border-radius)',
                    background: '#2a2a2a',
                    color: 'var(--text-light)'
                  }}
                  placeholder="Название разработчика"
                />
              </div>

              <div className="form-group">
                <label style={{ display: 'block', marginBottom: '0.5rem', color: '#cccccc' }}>
                  Издатель
                </label>
                <input
                  type="text"
                  value={formData.publisher}
                  onChange={(e) => setFormData({...formData, publisher: e.target.value})}
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '2px solid #333',
                    borderRadius: 'var(--border-radius)',
                    background: '#2a2a2a',
                    color: 'var(--text-light)'
                  }}
                  placeholder="Название издателя"
                />
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label style={{ display: 'block', marginBottom: '0.5rem', color: '#cccccc' }}>
                  Дата выхода
                </label>
                <input
                  type="date"
                  value={formData.releaseDate}
                  onChange={(e) => setFormData({...formData, releaseDate: e.target.value})}
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '2px solid #333',
                    borderRadius: 'var(--border-radius)',
                    background: '#2a2a2a',
                    color: 'var(--text-light)'
                  }}
                />
              </div>

              <div className="form-group">
                <label style={{ display: 'block', marginBottom: '0.5rem', color: '#cccccc' }}>
                  Платформа *
                </label>
                <select
                  value={formData.platform}
                  onChange={(e) => setFormData({...formData, platform: e.target.value})}
                  required
                  style={{
                    width: '100%',
                    padding: '0.75rem',
                    border: '2px solid #333',
                    borderRadius: 'var(--border-radius)',
                    background: '#2a2a2a',
                    color: 'var(--text-light)'
                  }}
                >
                  {platforms.map(platform => (
                    <option key={platform} value={platform}>{platform}</option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {/* Жанры */}
          <div className="form-section" style={{ marginBottom: '1.5rem' }}>
            <h3 style={{ color: '#00ff88', marginBottom: '1rem' }}>Жанры *</h3>
            
            <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem', flexWrap: 'wrap' }}>
              {formData.genres.map(genre => (
                <span
                  key={genre}
                  style={{
                    background: 'rgba(0, 255, 136, 0.2)',
                    color: '#00ff88',
                    padding: '0.5rem 1rem',
                    borderRadius: '20px',
                    fontSize: '0.8rem',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.5rem'
                  }}
                >
                  {genre}
                  <button
                    type="button"
                    onClick={() => handleRemoveGenre(genre)}
                    style={{
                      background: 'none',
                      border: 'none',
                      color: '#ff4444',
                      cursor: 'pointer',
                      fontSize: '1rem'
                    }}
                  >
                    ×
                  </button>
                </span>
              ))}
            </div>

            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <select
                value={selectedGenre}
                onChange={(e) => setSelectedGenre(e.target.value)}
                style={{
                  flex: 1,
                  padding: '0.75rem',
                  border: '2px solid #333',
                  borderRadius: 'var(--border-radius)',
                  background: '#2a2a2a',
                  color: 'var(--text-light)'
                }}
              >
                <option value="">Выберите жанр</option>
                {availableGenres.map(genre => (
                  <option key={genre} value={genre}>{genre}</option>
                ))}
              </select>
              <button
                type="button"
                onClick={handleAddGenre}
                className="btn btn-secondary"
                disabled={!selectedGenre}
                style={{ whiteSpace: 'nowrap' }}
              >
                Добавить жанр
              </button>
            </div>
          </div>

          {/* Изображение */}
          <div className="form-section" style={{ marginBottom: '1.5rem' }}>
            <h3 style={{ color: '#00ff88', marginBottom: '1rem' }}>Изображение</h3>
            
            <div className="form-group">
              <label style={{ display: 'block', marginBottom: '0.5rem', color: '#cccccc' }}>
                URL изображения
              </label>
              <input
                type="url"
                value={formData.imageUrl}
                onChange={(e) => setFormData({...formData, imageUrl: e.target.value})}
                style={{
                  width: '100%',
                  padding: '0.75rem',
                  border: '2px solid #333',
                  borderRadius: 'var(--border-radius)',
                  background: '#2a2a2a',
                  color: 'var(--text-light)'
                }}
                placeholder="https://example.com/image.jpg"
              />
            </div>
          </div>

          {/* Кнопки */}
          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
            <button
              type="button"
              onClick={onClose}
              className="btn btn-secondary"
              disabled={loading}
            >
              Отмена
            </button>
            <button
              type="submit"
              className="btn"
              disabled={loading}
            >
              {loading ? 'Сохранение...' : (game ? 'Обновить игру' : 'Создать игру')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default GameFormModal;
