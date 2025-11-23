import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { gameService } from '../services/gameService';
import GameCard from './GameCard';

function GameList() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('');
  const [error, setError] = useState('');
  const [searchParams, setSearchParams] = useSearchParams();

  const genres = [
    { value: '', label: 'Все жанры' },
    { value: 'ACTION', label: 'Экшен' },
    { value: 'RPG', label: 'RPG' },
    { value: 'STRATEGY', label: 'Стратегия' },
    { value: 'ADVENTURE', label: 'Приключения' },
    { value: 'SIMULATION', label: 'Симулятор' },
    { value: 'SPORTS', label: 'Спорт' }
  ];

  const loadGames = useCallback(async () => {
    try {
      setLoading(true);
      setError('');

      let gamesData;
      if (selectedGenre) {
        gamesData = await gameService.getGamesByGenre(selectedGenre);
      } else if (searchQuery) {
        gamesData = await gameService.searchGames(searchQuery);
      } else {
        gamesData = await gameService.getAllGames();
      }

      setGames(gamesData);
    } catch (error) {
      setError('Ошибка загрузки игр. Проверьте подключение к серверу.');
      console.error('Error loading games:', error);
    } finally {
      setLoading(false);
    }
  }, [searchQuery, selectedGenre]);

  useEffect(() => {
    // Обработка query параметров из URL
    const searchFromUrl = searchParams.get('search');
    if (searchFromUrl) {
      setSearchQuery(searchFromUrl);
    }
  }, [searchParams]);

  useEffect(() => {
    loadGames();
  }, [loadGames]);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      setSearchParams({ search: searchQuery });
    } else {
      setSearchParams({});
    }
  };

  const handleGenreChange = (genre) => {
    setSelectedGenre(genre);
    setSearchQuery('');
    setSearchParams({});
  };

  const handleReset = () => {
    setSearchQuery('');
    setSelectedGenre('');
    setSearchParams({});
  };

  if (loading) {
    return (
      <div className="container">
        <div className="loading">
          <div className="text-center">
            <h3>Загрузка игр...</h3>
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
          <div className="error-message" style={{ maxWidth: '500px', margin: '2rem auto' }}>
            <h3>Ошибка</h3>
            <p>{error}</p>
            <button onClick={loadGames} className="btn mt-2">
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
        <h1>Каталог игр</h1>
        <p style={{ color: '#cccccc' }}>Откройте для себя лучшие видеоигры</p>
      </div>

      {/* Поиск и фильтры */}
      <div className="card mb-4" style={{ padding: '2rem' }}>
        <form onSubmit={handleSearch} className="search-filters">
          <div className="search-section" style={{ marginBottom: '1.5rem' }}>
            <div style={{
              display: 'flex',
              gap: '1rem',
              alignItems: 'center',
              flexWrap: 'wrap'
            }}>
              <input
                type="text"
                placeholder="Поиск игр по названию..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="form-input"
                style={{ flex: '1', minWidth: '250px' }}
              />
              <button type="submit" className="btn">
                Найти
              </button>
            </div>
          </div>

          <div className="filters-section" style={{ marginBottom: '1.5rem' }}>
            <div className="genre-filters">
              <h4 style={{
                marginBottom: '1rem',
                color: 'var(--primary-color)',
                fontSize: '1.1rem'
              }}>
                Фильтр по жанрам:
              </h4>
              <div className="genre-buttons">
                {genres.map(genre => (
                  <button
                    key={genre.value}
                    type="button"
                    className={`genre-btn ${selectedGenre === genre.value ? 'active' : ''}`}
                    onClick={() => handleGenreChange(genre.value)}
                  >
                    {genre.label}
                  </button>
                ))}
              </div>
            </div>
          </div>

          <div className="actions-section">
            <button
              type="button"
              onClick={handleReset}
              className="btn btn-secondary"
            >
              Сбросить все
            </button>
          </div>
        </form>
      </div>

      {/* Информация о результатах */}
      {(searchQuery || selectedGenre) && (
        <div className="card mb-4">
          <div className="results-info" style={{ padding: '1rem 1.5rem' }}>
            <p style={{ margin: 0 }}>
              Найдено игр: <strong>{games.length}</strong>
              {searchQuery && ` по запросу "${searchQuery}"`}
              {selectedGenre && ` в жанре "${genres.find(g => g.value === selectedGenre)?.label}"`}
            </p>
          </div>
        </div>
      )}

      {/* Сетка игр */}
      {games.length > 0 ? (
        <div className="games-grid">
          {games.map(game => (
            <GameCard key={game.id} game={game} />
          ))}
        </div>
      ) : (
        <div className="text-center">
          <div className="card" style={{
            maxWidth: '400px',
            margin: '2rem auto',
            padding: '2rem'
          }}>
            <h3>Игры не найдены</h3>
            <p style={{ color: '#cccccc', marginBottom: '1rem' }}>
              {searchQuery || selectedGenre
                ? 'Попробуйте изменить параметры поиска'
                : 'В каталоге пока нет игр'
              }
            </p>
            {(searchQuery || selectedGenre) && (
              <button onClick={handleReset} className="btn">
                Показать все игры
              </button>
            )}
          </div>
        </div>
      )}

      <style jsx>{`
        .search-filters {
          display: flex;
          flex-direction: column;
        }

        .genre-buttons {
          display: flex;
          flex-wrap: wrap;
          gap: 0.5rem;
        }

        .genre-btn {
          padding: 0.5rem 1rem;
          border: 2px solid #333;
          background: #2a2a2a;
          color: var(--text-light);
          border-radius: 20px;
          cursor: pointer;
          transition: all 0.3s ease;
          font-size: 0.9rem;
        }

        .genre-btn:hover {
          border-color: var(--primary-color);
        }

        .genre-btn.active {
          background: var(--primary-color);
          color: var(--background-dark);
          border-color: var(--primary-color);
        }

        .games-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
          gap: 1.5rem;
          width: 100%;
        }

        /* Адаптивность */
        @media (max-width: 768px) {
          .games-grid {
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 1rem;
          }

          .search-section > div {
            flex-direction: column;
          }

          .search-section .form-input {
            min-width: auto;
            width: 100%;
          }

          .genre-buttons {
            justify-content: center;
          }
        }

        @media (max-width: 480px) {
          .games-grid {
            grid-template-columns: 1fr;
          }

          .card {
            padding: 1rem;
          }
        }
      `}</style>
    </div>
  );
}

export default GameList;
