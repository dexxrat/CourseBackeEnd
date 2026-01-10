package com.example.gamestore.controller;

import com.example.gamestore.dto.GameDTO;
import com.example.gamestore.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameController {
    private final GameService gameService;

    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGames() {
        List<GameDTO> games = gameService.getAllActiveGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable Long id) {
        GameDTO game = gameService.getGameById(id);
        return ResponseEntity.ok(game);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GameDTO>> searchGames(@RequestParam String query) {
        List<GameDTO> games = gameService.searchGames(query);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<GameDTO>> getGamesByGenre(@PathVariable String genre) {
        List<GameDTO> games = gameService.getGamesByGenre(genre);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<GameDTO>> getGamesByPlatform(@PathVariable String platform) {
        List<GameDTO> games = gameService.getGamesByPlatform(platform);
        return ResponseEntity.ok(games);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDTO> createGame(@RequestBody GameDTO gameDTO) {
        GameDTO createdGame = gameService.createGame(gameDTO);
        return ResponseEntity.ok(createdGame);
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDTO> updateGame(@PathVariable Long id, @RequestBody GameDTO gameDTO) {
        GameDTO updatedGame = gameService.updateGame(id, gameDTO);
        return ResponseEntity.ok(updatedGame);
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok().build();
    }
}