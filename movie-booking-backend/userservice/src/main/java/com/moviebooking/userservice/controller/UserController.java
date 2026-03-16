package com.moviebooking.userservice.controller;

import com.moviebooking.userservice.dtos.*;
import com.moviebooking.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/sync")
    public ResponseEntity<UserResponse> syncUser(@Valid @RequestBody SyncUserRequest request) {
        return ResponseEntity.ok(userService.syncUser(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.getCurrentMe(jwt.getSubject())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable String id) {
        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            Pageable pageable) {
        return ResponseEntity.ok(
                userService.getAllUser(pageable)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UserUpdateDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.updateProfile(dto, jwt.getSubject())
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<Void> disableUser(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {
        userService.disableProfile(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}