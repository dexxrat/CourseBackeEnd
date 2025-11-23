package com.example.gamestore.controller;

import com.example.gamestore.dto.GameDTO;
import com.example.gamestore.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class GameController {

    private final GameService gameService;

    @GetMapping
    public ResponseEntity<?> getAllGames() {
        try {
            log.info("=== GET /api/games called ===");
            List<GameDTO> games = gameService.getAllActiveGames();
            log.info("Successfully loaded {} games", games.size());
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            log.error("ERROR in getAllGames: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage(),
                            "timestamp", System.currentTimeMillis()
                    ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGameById(@PathVariable Long id) {
        try {
            log.info("=== GET /api/games/{} called ===", id);
            GameDTO game = gameService.getGameById(id);
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            log.warn("Game not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("ERROR in getGameById: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchGames(@RequestParam String query) {
        try {
            log.info("=== GET /api/games/search called with query: {} ===", query);
            List<GameDTO> games = gameService.searchGames(query);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            log.error("ERROR in searchGames: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<?> getGamesByGenre(@PathVariable String genre) {
        try {
            log.info("=== GET /api/games/genre/{} called ===", genre);
            List<GameDTO> games = gameService.getGamesByGenre(genre);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            log.error("ERROR in getGamesByGenre: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/platform/{platform}")
    public ResponseEntity<?> getGamesByPlatform(@PathVariable String platform) {
        try {
            log.info("=== GET /api/games/platform/{} called ===", platform);
            List<GameDTO> games = gameService.getGamesByPlatform(platform);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            log.error("ERROR in getGamesByPlatform: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createGame(@RequestBody GameDTO gameDTO) {
        try {
            log.info("=== POST /api/games/admin called ===");
            log.info("Creating new game: {}", gameDTO.getTitle());

            GameDTO createdGame = gameService.createGame(gameDTO);
            log.info("Game created successfully with ID: {}", createdGame.getId());

            return ResponseEntity.ok(createdGame);
        } catch (Exception e) {
            log.error("ERROR in createGame: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Bad Request",
                            "message", e.getMessage()
                    ));
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateGame(@PathVariable Long id, @RequestBody GameDTO gameDTO) {
        try {
            log.info("=== PUT /api/games/admin/{} called ===", id);
            log.info("Updating game: {}", gameDTO.getTitle());

            GameDTO updatedGame = gameService.updateGame(id, gameDTO);
            log.info("Game updated successfully: {}", updatedGame.getTitle());

            return ResponseEntity.ok(updatedGame);
        } catch (RuntimeException e) {
            log.warn("Game not found for update with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("ERROR in updateGame: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Bad Request",
                            "message", e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteGame(@PathVariable Long id) {
        try {
            log.info("=== DELETE /api/games/admin/{} called ===", id);

            gameService.deleteGame(id);
            log.info("Game soft deleted successfully with ID: {}", id);

            return ResponseEntity.ok(Map.of(
                    "message", "Game deleted successfully",
                    "id", id
            ));
        } catch (RuntimeException e) {
            log.warn("Game not found for deletion with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("ERROR in deleteGame: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Bad Request",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Backend is working perfectly!",
                "timestamp", System.currentTimeMillis()
        ));
    }
}