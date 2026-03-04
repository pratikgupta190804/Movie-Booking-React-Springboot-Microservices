package com.moviebooking.theatre.controller;

import com.moviebooking.theatre.dtos.TheatreRequestDto;
import com.moviebooking.theatre.dtos.TheatreResponseDto;
import com.moviebooking.theatre.dtos.TheatreUpdateDto;
import com.moviebooking.theatre.service.TheatreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/theatres")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService theatreService;

    @PostMapping
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<TheatreResponseDto> createTheatre(
            @RequestBody TheatreRequestDto requestDto,
            @RequestHeader("X-User-Id") String ownerId
    ) {
        return ResponseEntity.ok(
                theatreService.createTheatre(requestDto, ownerId)
        );
    }

    @PutMapping("/{theatreId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<TheatreResponseDto> updateTheatre(
            @PathVariable String theatreId,
            @RequestBody TheatreUpdateDto updateDto,
            @RequestHeader("X-User-Id") String ownerId
    ) {
        return ResponseEntity.ok(
                theatreService.updateTheatre(theatreId, updateDto, ownerId)
        );
    }

    @PatchMapping("/{theatreId}/activate")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<TheatreResponseDto> activateTheatre(
            @PathVariable String theatreId,
            @RequestHeader("X-User-Id") String ownerId
    ) {
        return ResponseEntity.ok(
                theatreService.activateTheatre(theatreId, ownerId)
        );
    }

    @PatchMapping("/{theatreId}/deactivate")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<Void> deactivateTheatre(
            @PathVariable String theatreId,
            @RequestHeader("X-User-Id") String ownerId
    ) {
        theatreService.deactivateTheatre(theatreId, ownerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{theatreId}")
    public ResponseEntity<TheatreResponseDto> getTheatreById(
            @PathVariable String theatreId
    ) {
        return ResponseEntity.ok(
                theatreService.getTheatreById(theatreId)
        );
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<Page<TheatreResponseDto>> getByCity(
            @PathVariable String city,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                theatreService.getTheatresByCity(city, pageable)
        );
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<Page<TheatreResponseDto>> getByBrand(
            @PathVariable String brand,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                theatreService.getTheatresByBrand(brand, pageable)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TheatreResponseDto>> searchByName(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                theatreService.searchTheatresByName(keyword, pageable)
        );
    }

    @GetMapping("/near")
    public ResponseEntity<Page<TheatreResponseDto>> getNear(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radius,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                theatreService.getTheatresNear(lat, lng, radius, pageable)
        );
    }
}