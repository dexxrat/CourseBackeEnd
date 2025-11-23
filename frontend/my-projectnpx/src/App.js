import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import Header from './components/Header';
import Home from './components/Home';
import GameList from './components/GameList';
import GameDetail from './components/GameDetail';
import Cart from './components/Cart';
import Login from './components/Login';
import Register from './components/Register';
import OrderHistory from './components/OrderHistory';
import AdminPanel from './components/AdminPanel';
import './App.css';

// Компонент для защиты маршрутов - ДОЛЖЕН БЫТЬ ВНУТРИ AuthProvider
const ProtectedRoute = ({ children, requireAdmin = false }) => {
  const { isAuthenticated, isAdmin } = useAuth();

  console.log('ProtectedRoute check:', {
    isAuthenticated: isAuthenticated(),
    isAdmin: isAdmin(),
    requireAdmin
  });

  if (!isAuthenticated()) {
    console.log('ProtectedRoute: Not authenticated, redirecting to login');
    return <Navigate to="/login" replace />;
  }

  if (requireAdmin && !isAdmin()) {
    console.log('ProtectedRoute: Not admin, redirecting to home');
    return <Navigate to="/" replace />;
  }

  console.log('ProtectedRoute: Access granted');
  return children;
};

function App() {
  return (
    <Router>
      <AuthProvider>
        <CartProvider>
          <div className="App">
            <Header />
            <main>
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/games" element={<GameList />} />
                <Route path="/games/:id" element={<GameDetail />} />
                <Route path="/cart" element={<Cart />} />
                <Route
                  path="/orders"
                  element={
                    <ProtectedRoute>
                      <OrderHistory />
                    </ProtectedRoute>
                  }
                />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route
                  path="/admin"
                  element={
                    <ProtectedRoute requireAdmin={true}>
                      <AdminPanel />
                    </ProtectedRoute>
                  }
                />
                {/* Резервный маршрут для несуществующих страниц */}
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </main>
          </div>
        </CartProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;