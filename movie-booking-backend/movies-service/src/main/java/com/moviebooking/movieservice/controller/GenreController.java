package com.moviebooking.movieservice.controller;

import com.moviebooking.movieservice.dtos.GenreDetailsDto;
import com.moviebooking.movieservice.dtos.GenreDto;
import com.moviebooking.movieservice.dtos.GenreRequestDto;
import com.moviebooking.movieservice.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<GenreDto> createGenre(
            @Valid @RequestBody GenreRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        GenreDto response = genreService.createGenre(requestDto.getName(), userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GenreDto>> getAllGenres() {
        List<GenreDto> response = genreService.getAllGenres();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable String genreId) {
        GenreDto response = genreService.getGenreById(genreId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{genreId}/details")
    public ResponseEntity<GenreDetailsDto> getGenreDetails(@PathVariable String genreId) {
        GenreDetailsDto response = genreService.getGenreDetailsById(genreId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{genreId}")
    public ResponseEntity<GenreDto> updateGenre(
            @PathVariable String genreId,
            @Valid @RequestBody GenreRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        GenreDto response = genreService.updateGenre(genreId, requestDto.getName(), userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<Void> deleteGenre(
            @PathVariable String genreId,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        genreService.deleteGenre(genreId, userId);
        return ResponseEntity.noContent().build();
    }
}
