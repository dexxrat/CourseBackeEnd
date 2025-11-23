import React from 'react';
import { Link } from 'react-router-dom';

function Home() {
  return (
    <div className="container">
      <section className="text-center" style={{ padding: '4rem 1rem' }}>
        <div className="card" style={{
          background: 'linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%)',
          border: '2px solid #00ff88',
          maxWidth: '600px',
          margin: '0 auto',
          padding: '3rem 2rem'
        }}>
          <h1 style={{
            fontSize: '2.5rem',
            marginBottom: '1rem',
            background: 'linear-gradient(45deg, #00ff88, #66ffb3)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent'
          }}>
            Добро пожаловать в ZenGame
          </h1>
          <p style={{
            fontSize: '1.2rem',
            color: '#cccccc',
            marginBottom: '2rem',
            lineHeight: '1.6'
          }}>
            Лучшие видеоигры для всех платформ — легко, удобно, выгодно.
          </p>
          <Link to="/games" className="btn" style={{
            fontSize: '1.1rem',
            padding: '1rem 2rem'
          }}>
            Смотреть каталог
          </Link>
        </div>
      </section>
    </div>
  );
}

export default Home;
