package com.example.gamestore.repository;

import com.example.gamestore.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByActiveTrue();
    Optional<Game> findByIdAndActiveTrue(Long id);
    List<Game> findByTitleContainingIgnoreCaseAndActiveTrue(String title);

    @Query("SELECT g FROM Game g WHERE :genre MEMBER OF g.genres AND g.active = true")
    List<Game> findByGenreAndActiveTrue(@Param("genre") String genre);

    List<Game> findByPlatformAndActiveTrue(String platform);
}