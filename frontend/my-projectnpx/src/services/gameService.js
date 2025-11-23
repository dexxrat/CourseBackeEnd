import api from './api';

export const gameService = {
  async getAllGames() {
    try {
      console.log('Fetching all games...');
      const games = await api.get('/api/games');
      console.log(`Successfully fetched ${games?.length || 0} games`);
      return games || [];
    } catch (error) {
      console.error('Error fetching all games:', error);
      throw error;
    }
  },

  async getGameById(id) {
    try {
      console.log(`Fetching game with id: ${id}`);
      const game = await api.get(`/api/games/${id}`);
      return game;
    } catch (error) {
      console.error(`Error fetching game ${id}:`, error);
      throw error;
    }
  },

  async searchGames(query) {
    try {
      console.log(`Searching games with query: ${query}`);
      const games = await api.get(`/api/games/search?query=${encodeURIComponent(query)}`);
      return games || [];
    } catch (error) {
      console.error(`Error searching games with query "${query}":`, error);
      throw error;
    }
  },

  async getGamesByGenre(genre) {
    try {
      console.log(`Fetching games by genre: ${genre}`);
      const games = await api.get(`/api/games/genre/${encodeURIComponent(genre)}`);
      return games || [];
    } catch (error) {
      console.error(`Error fetching games by genre "${genre}":`, error);
      throw error;
    }
  },

  async getGamesByPlatform(platform) {
    try {
      console.log(`Fetching games by platform: ${platform}`);
      const games = await api.get(`/api/games/platform/${encodeURIComponent(platform)}`);
      return games || [];
    } catch (error) {
      console.error(`Error fetching games by platform "${platform}":`, error);
      throw error;
    }
  },

  // НОВЫЕ МЕТОДЫ ДЛЯ АДМИН-ПАНЕЛИ
  async createGame(gameData) {
    try {
      console.log('Creating new game:', gameData);
      const response = await api.post('/api/games/admin', gameData);
      console.log('Game created successfully:', response);
      return response;
    } catch (error) {
      console.error('Error creating game:', error);
      throw error;
    }
  },

  async updateGame(id, gameData) {
    try {
      console.log(`Updating game ${id}:`, gameData);
      const response = await api.put(`/api/games/admin/${id}`, gameData);
      console.log('Game updated successfully:', response);
      return response;
    } catch (error) {
      console.error(`Error updating game ${id}:`, error);
      throw error;
    }
  },

  async deleteGame(id) {
    try {
      console.log(`Deleting game ${id}`);
      const response = await api.delete(`/api/games/admin/${id}`);
      console.log('Game deleted successfully');
      return response;
    } catch (error) {
      console.error(`Error deleting game ${id}:`, error);
      throw error;
    }
  }
};
