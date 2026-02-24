package com.moviebooking.userservice.controller;

import com.moviebooking.userservice.dto.*;
import com.moviebooking.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/sync")
    public ResponseEntity<UserResponse> syncUser(
            @Valid @RequestBody UserSyncRequest request,
            @RequestHeader(value = "X-Gateway-Secret", required = false) String gatewaySecret) {

        log.info("Sync user request received - email: {}", request.getEmail());
        UserResponse response = userService.syncUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        log.info("Get user by id request - id: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Get user by email request - email: {}", email);
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequest request,
            @RequestHeader("X-User-Id") String requestingUserId,
            @RequestHeader("X-User-Roles") String requestingUserRoles) {

        log.info("Update user request - id: {}, requestingUserId: {}", id, requestingUserId);
        UserResponse response = userService.updateUser(id, request, requestingUserId, requestingUserRoles);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable String id,
            @Valid @RequestBody AssignRoleDTO request,
            @RequestHeader("X-User-Roles") String requestingUserRoles) {

        log.info("Assign role request - userId: {}, newRole: {}", id, request.getRole());
        UserResponse response = userService.assignRole(id, request, requestingUserRoles);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody StatusUpdateRequest request,
            @RequestHeader("X-User-Roles") String requestingUserRoles) {

        log.info("Update status request - userId: {}, enabled: {}", id, request.getEnabled());
        UserResponse response = userService.updateStatus(id, request, requestingUserRoles);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String requestingUserId,
            @RequestHeader("X-User-Roles") String requestingUserRoles) {

        log.info("Delete user request - userId: {}, requestingUserId: {}", id, requestingUserId);
        userService.deleteUser(id, requestingUserId, requestingUserRoles);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestHeader("X-User-Roles") String requestingUserRoles) {

        log.info("Get all users request - page: {}, size: {}", page, size);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<UserResponse> response = userService.getAllUsers(pageable, requestingUserRoles);
        return ResponseEntity.ok(response);
    }
}
