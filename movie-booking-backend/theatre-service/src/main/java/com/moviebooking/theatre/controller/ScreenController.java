package com.moviebooking.theatre.controller;

import com.moviebooking.theatre.dtos.ScreenLayoutRequestDto;
import com.moviebooking.theatre.dtos.ScreenResponseDto;
import com.moviebooking.theatre.service.ScreenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping("/theatre/{theatreId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<ScreenResponseDto> createScreen(
            @PathVariable String theatreId,
            @Valid @RequestBody ScreenLayoutRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt) {

        String ownerId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(screenService.createScreenWithLayout(theatreId, requestDto, ownerId));
    }

    @PutMapping("/{screenId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<ScreenResponseDto> updateScreen(
            @PathVariable String screenId,
            @Valid @RequestBody ScreenLayoutRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt) {

        String ownerId = jwt.getSubject();
        return ResponseEntity.ok(
                screenService.updateScreenLayout(screenId, requestDto, ownerId));
    }

    @GetMapping("/{screenId}")
    public ResponseEntity<ScreenResponseDto> getScreen(@PathVariable String screenId) {
        return ResponseEntity.ok(screenService.getScreenWithSeats(screenId));
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<List<ScreenResponseDto>> getScreenByTheatre(@PathVariable String theatreId) {
        return ResponseEntity.ok(screenService.getScreensWithSeatsByTheatreId(theatreId));
    }
}