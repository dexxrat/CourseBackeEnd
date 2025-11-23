package com.example.gamestore.service;

import com.example.gamestore.dto.GameDTO;

import java.util.List;

public interface GameService {
    List<GameDTO> getAllActiveGames();
    GameDTO getGameById(Long id);
    List<GameDTO> searchGames(String query);
    List<GameDTO> getGamesByGenre(String genre);
    List<GameDTO> getGamesByPlatform(String platform);
    GameDTO createGame(GameDTO gameDTO);
    GameDTO updateGame(Long id, GameDTO gameDTO);
    void deleteGame(Long id);
}