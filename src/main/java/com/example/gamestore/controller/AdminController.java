package com.example.gamestore.controller;

import com.example.gamestore.dto.GameDTO;
import com.example.gamestore.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final GameService gameService;

    @PostMapping("/games")
    public ResponseEntity<GameDTO> createGame(@Valid @RequestBody GameDTO gameDTO) {
        return ResponseEntity.ok(gameService.createGame(gameDTO));
    }

    @PutMapping("/games/{id}")
    public ResponseEntity<GameDTO> updateGame(@PathVariable Long id, @Valid @RequestBody GameDTO gameDTO) {
        return ResponseEntity.ok(gameService.updateGame(id, gameDTO));
    }

    @DeleteMapping("/games/{id}")
    public ResponseEntity<String> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok("Game deleted successfully");
    }
}