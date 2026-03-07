package com.moviebooking.show.controller;

import com.moviebooking.show.dtos.CreateShowRequestDto;
import com.moviebooking.show.dtos.ShowResponseDto;
import com.moviebooking.show.dtos.UpdateShowRequestDto;
import com.moviebooking.show.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @PostMapping
    @PreAuthorize("hasRole('THEATRE_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ShowResponseDto> createShow(
            @Valid @RequestBody CreateShowRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        ShowResponseDto response = showService.createShow(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{showId}")
    @PreAuthorize("hasRole('THEATRE_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ShowResponseDto> updateShow(
            @PathVariable String showId,
            @Valid @RequestBody UpdateShowRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        ShowResponseDto response = showService.updateShow(showId, requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{showId}")
    public ResponseEntity<ShowResponseDto> getShowById(
            @PathVariable String showId
    ) {
        ShowResponseDto response = showService.getShowById(showId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowResponseDto>> getShowsByMovie(
            @PathVariable String movieId
    ) {
        List<ShowResponseDto> response = showService.getShowsByMovie(movieId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<List<ShowResponseDto>> getShowsByTheatre(
            @PathVariable String theatreId
    ) {
        List<ShowResponseDto> response = showService.getShowsByTheatre(theatreId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/screen/{screenId}")
    public ResponseEntity<List<ShowResponseDto>> getShowsByScreen(
            @PathVariable String screenId
    ) {
        List<ShowResponseDto> response = showService.getShowsByScreen(screenId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movie/{movieId}/theatre/{theatreId}")
    public ResponseEntity<List<ShowResponseDto>> getShowsByMovieAndTheatre(
            @PathVariable String movieId,
            @PathVariable String theatreId,
            @RequestParam(required = false) LocalDate date
    ) {
        List<ShowResponseDto> response = showService.getShowsByMovieAndTheatre(movieId, theatreId, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ShowResponseDto>> getShowsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    ) {
        List<ShowResponseDto> response = showService.getShowsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{showId}")
    @PreAuthorize("hasRole('THEATRE_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelShow(
            @PathVariable String showId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        showService.cancelShow(showId);
        return ResponseEntity.noContent().build();
    }
}
