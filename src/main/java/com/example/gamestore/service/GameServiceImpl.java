package com.example.gamestore.service;

import com.example.gamestore.dto.GameDTO;
import com.example.gamestore.model.Game;
import com.example.gamestore.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getAllActiveGames() {
        try {
            log.info("Getting all active games from database...");

            List<Game> games = gameRepository.findByActiveTrue();
            log.info("Found {} active games in database", games.size());

            return games.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error in getAllActiveGames: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load games: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GameDTO getGameById(Long id) {
        try {
            Game game = gameRepository.findByIdAndActiveTrue(id)
                    .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

            return convertToDTO(game);
        } catch (Exception e) {
            log.error("Error getting game by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to load game: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> searchGames(String query) {
        try {
            List<Game> games = gameRepository.findByTitleContainingIgnoreCaseAndActiveTrue(query);
            return games.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching games with query '{}': {}", query, e.getMessage());
            throw new RuntimeException("Failed to search games: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getGamesByGenre(String genre) {
        try {
            List<Game> games = gameRepository.findByGenreAndActiveTrue(genre);
            return games.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting games by genre '{}': {}", genre, e.getMessage());
            throw new RuntimeException("Failed to load games by genre: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getGamesByPlatform(String platform) {
        try {
            List<Game> games = gameRepository.findByPlatformAndActiveTrue(platform);
            return games.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting games by platform '{}': {}", platform, e.getMessage());
            throw new RuntimeException("Failed to load games by platform: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public GameDTO createGame(GameDTO gameDTO) {
        try {
            Game game = convertToEntity(gameDTO);
            game.setActive(true);
            Game savedGame = gameRepository.save(game);
            return convertToDTO(savedGame);
        } catch (Exception e) {
            log.error("Error creating game: {}", e.getMessage());
            throw new RuntimeException("Failed to create game: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public GameDTO updateGame(Long id, GameDTO gameDTO) {
        try {
            Game existingGame = gameRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

            // Обновляем поля
            existingGame.setTitle(gameDTO.getTitle());
            existingGame.setDescription(gameDTO.getDescription());
            existingGame.setDeveloper(gameDTO.getDeveloper());
            existingGame.setPublisher(gameDTO.getPublisher());
            existingGame.setReleaseDate(gameDTO.getReleaseDate());
            existingGame.setPlatform(gameDTO.getPlatform());
            existingGame.setPrice(gameDTO.getPrice());
            existingGame.setDiscountPrice(gameDTO.getDiscountPrice());
            existingGame.setImageUrl(gameDTO.getImageUrl());

            if (gameDTO.getGenres() != null) {
                existingGame.setGenres(gameDTO.getGenres());
            }

            Game updatedGame = gameRepository.save(existingGame);
            return convertToDTO(updatedGame);
        } catch (Exception e) {
            log.error("Error updating game {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update game: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteGame(Long id) {
        try {
            Game game = gameRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
            game.setActive(false);
            gameRepository.save(game);
            log.info("Soft deleted game with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting game {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete game: " + e.getMessage());
        }
    }

    // Вспомогательный метод для конвертации Game в GameDTO
    private GameDTO convertToDTO(Game game) {
        GameDTO dto = new GameDTO();
        dto.setId(game.getId());
        dto.setTitle(game.getTitle());
        dto.setDescription(game.getDescription());
        dto.setDeveloper(game.getDeveloper());
        dto.setPublisher(game.getPublisher());
        dto.setReleaseDate(game.getReleaseDate());
        dto.setPlatform(game.getPlatform());
        dto.setPrice(game.getPrice());
        dto.setDiscountPrice(game.getDiscountPrice());
        dto.setImageUrl(game.getImageUrl());

        // ИСПРАВЛЕНО: жанры уже загружены благодаря EAGER fetch
        dto.setGenres(game.getGenres());

        dto.setFinalPrice(game.getFinalPrice());
        dto.setHasDiscount(game.hasDiscount());
        dto.setDiscountPercentage(game.getDiscountPercentage());

        return dto;
    }

    private Game convertToEntity(GameDTO dto) {
        Game game = new Game();
        game.setTitle(dto.getTitle());
        game.setDescription(dto.getDescription());
        game.setDeveloper(dto.getDeveloper());
        game.setPublisher(dto.getPublisher());
        game.setReleaseDate(dto.getReleaseDate());
        game.setPlatform(dto.getPlatform());
        game.setPrice(dto.getPrice());
        game.setDiscountPrice(dto.getDiscountPrice());
        game.setImageUrl(dto.getImageUrl());

        if (dto.getGenres() != null) {
            game.setGenres(dto.getGenres());
        }

        return game;
    }
}