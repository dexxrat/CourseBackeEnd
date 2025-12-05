package com.example.gamestore.controller;

import com.example.gamestore.dto.GameDTO;
import com.example.gamestore.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class GameController {

    private final GameService gameService;

    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGames() {
        log.info("=== GET /api/games called ===");
        List<GameDTO> games = gameService.getAllActiveGames();
        log.info("Successfully loaded {} games", games.size());
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable Long id) {
        log.info("=== GET /api/games/{} called ===", id);
        GameDTO game = gameService.getGameById(id);
        return ResponseEntity.ok(game);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GameDTO>> searchGames(@RequestParam String query) {
        log.info("=== GET /api/games/search called with query: {} ===", query);
        List<GameDTO> games = gameService.searchGames(query);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<GameDTO>> getGamesByGenre(@PathVariable String genre) {
        log.info("=== GET /api/games/genre/{} called ===", genre);
        List<GameDTO> games = gameService.getGamesByGenre(genre);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<GameDTO>> getGamesByPlatform(@PathVariable String platform) {
        log.info("=== GET /api/games/platform/{} called ===", platform);
        List<GameDTO> games = gameService.getGamesByPlatform(platform);
        return ResponseEntity.ok(games);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDTO> createGame(@RequestBody GameDTO gameDTO) {
        log.info("=== POST /api/games/admin called ===");
        log.info("Creating new game: {}", gameDTO.getTitle());

        GameDTO createdGame = gameService.createGame(gameDTO);
        log.info("Game created successfully with ID: {}", createdGame.getId());

        return ResponseEntity.ok(createdGame);
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDTO> updateGame(@PathVariable Long id, @RequestBody GameDTO gameDTO) {
        log.info("=== PUT /api/games/admin/{} called ===", id);
        log.info("Updating game: {}", gameDTO.getTitle());

        GameDTO updatedGame = gameService.updateGame(id, gameDTO);
        log.info("Game updated successfully: {}", updatedGame.getTitle());

        return ResponseEntity.ok(updatedGame);
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        log.info("=== DELETE /api/games/admin/{} called ===", id);

        gameService.deleteGame(id);
        log.info("Game soft deleted successfully with ID: {}", id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Backend is working perfectly!");
    }
}