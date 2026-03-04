package com.moviebooking.movieservice.controller;

import com.moviebooking.movieservice.dtos.ActorDetailsDto;
import com.moviebooking.movieservice.dtos.ActorDto;
import com.moviebooking.movieservice.dtos.ActorRequestDto;
import com.moviebooking.movieservice.service.ActorService;
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
@RequestMapping("/api/actors")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActorDto> createActor(
            @Valid @RequestBody ActorRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        ActorDto response = actorService.createActor(
                requestDto.getName(),
                requestDto.getDateOfBirth(),
                requestDto.getBio(),
                requestDto.getImageUrl(),
                userId
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ActorDto>> getAllActors() {
        List<ActorDto> response = actorService.getAllActors();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{actorId}")
    public ResponseEntity<ActorDto> getActorById(@PathVariable String actorId) {
        ActorDto response = actorService.getActorById(actorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{actorId}/details")
    public ResponseEntity<ActorDetailsDto> getActorDetails(@PathVariable String actorId) {
        ActorDetailsDto response = actorService.getActorDetailsById(actorId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{actorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActorDto> updateActor(
            @PathVariable String actorId,
            @Valid @RequestBody ActorRequestDto requestDto,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        ActorDto response = actorService.updateActor(
                actorId,
                requestDto.getName(),
                requestDto.getDateOfBirth(),
                requestDto.getBio(),
                requestDto.getImageUrl(),
                userId
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{actorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteActor(
            @PathVariable String actorId,
            @AuthenticationPrincipal Jwt jwt) {
        
        String userId = jwt.getSubject();
        actorService.deleteActor(actorId, userId);
        return ResponseEntity.noContent().build();
    }
}
