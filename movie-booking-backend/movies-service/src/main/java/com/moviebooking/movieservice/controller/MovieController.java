package com.moviebooking.movieservice.controller;

import com.moviebooking.movieservice.dtos.MovieRequestDto;
import com.moviebooking.movieservice.dtos.MovieResponseDto;
import com.moviebooking.movieservice.dtos.MovieUpdateDto;
import com.moviebooking.movieservice.entities.MovieStatus;
import com.moviebooking.movieservice.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponseDto> createMovie(
            @Valid @RequestBody MovieRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        MovieResponseDto response = movieService.addMovie(requestDto, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieResponseDto> getMovieById(@PathVariable String movieId) {
        MovieResponseDto response = movieService.getMovieById(movieId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<MovieResponseDto> getMovieBySlug(@PathVariable String slug) {
        MovieResponseDto response = movieService.getMovieBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<MovieResponseDto>> getMoviesByRating(@PathVariable BigDecimal rating) {
        List<MovieResponseDto> response = movieService.getMoviesByRatingGreaterThan(rating);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MovieResponseDto>> getMoviesByStatus(@PathVariable MovieStatus status) {
        List<MovieResponseDto> response = movieService.getMoviesByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponseDto>> searchMovies(@RequestParam String keyword) {
        List<MovieResponseDto> response = movieService.searchMoviesByTitle(keyword);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponseDto> updateMovie(
            @PathVariable String movieId,
            @Valid @RequestBody MovieUpdateDto updateDto,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        MovieResponseDto response = movieService.updateMovie(movieId, updateDto, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{movieId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieResponseDto> updateMovieStatus(
            @PathVariable String movieId,
            @RequestParam MovieStatus status,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        MovieResponseDto response = movieService.updateStatus(movieId, status, userId);
        return ResponseEntity.ok(response);
    }
}
