package com.moviebooking.userservice.service.impl;

import com.moviebooking.userservice.dto.*;
import com.moviebooking.userservice.exception.ConflictException;
import com.moviebooking.userservice.exception.ForbiddenException;
import com.moviebooking.userservice.exception.ResourceNotFoundException;
import com.moviebooking.userservice.model.Role;
import com.moviebooking.userservice.model.User;
import com.moviebooking.userservice.repo.UserRepository;
import com.moviebooking.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserResponse syncUser(UserSyncRequest request) {
        log.info("Syncing user - keycloakId: {}, email: {}, provider: {}",
                request.getKeycloakId(), request.getEmail(), request.getProvider());

        User user = userRepository.findById(request.getKeycloakId())
                .map(existingUser -> {
                    existingUser.setName(request.getName());
                    existingUser.setImage(request.getImage());
                    existingUser.setLastLoginAt(LocalDateTime.now());
                    log.info("Updated existing user - id: {}", existingUser.getId());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    if (userRepository.existsByEmail(request.getEmail())) {
                        log.error("Email already exists: {}", request.getEmail());
                        throw new ConflictException("User with email " + request.getEmail() + " already exists");
                    }

                    User newUser = User.builder()
                            .id(request.getKeycloakId())
                            .name(request.getName())
                            .email(request.getEmail())
                            .image(request.getImage())
                            .provider(request.getProvider())
                            .providerId(request.getProviderId())
                            .role(Role.ROLE_CUSTOMER)
                            .enabled(true)
                            .lastLoginAt(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .build();

                    log.info("Created new user - id: {}, email: {}", newUser.getId(), newUser.getEmail());
                    return userRepository.save(newUser);
                });

        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserById(String id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(String id, UserUpdateRequest request,
            String requestingUserId, String requestingUserRoles) {
        log.info("Updating user - id: {}, requestingUserId: {}", id, requestingUserId);

        if (!id.equals(requestingUserId) && !hasRole(requestingUserRoles, "ADMIN")) {
            log.warn("Access denied - userId: {} tried to update userId: {}", requestingUserId, id);
            throw new ForbiddenException("You don't have permission to update this user");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setName(request.getName());
        if (request.getImage() != null) {
            user.setImage(request.getImage());
        }

        user = userRepository.save(user);
        log.info("User updated successfully - id: {}", user.getId());

        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse assignRole(String id, AssignRoleDTO request, String requestingUserRoles) {
        log.info("Assigning role - userId: {}, newRole: {}", id, request.getRole());

        if (!hasRole(requestingUserRoles, "ADMIN")) {
            log.warn("Access denied - non-admin tried to assign role");
            throw new ForbiddenException("Only admins can assign roles");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        Role oldRole = user.getRole();
        user.setRole(request.getRole());
        user = userRepository.save(user);

        log.info("Role changed - userId: {}, oldRole: {}, newRole: {}", id, oldRole, request.getRole());

        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateStatus(String id, StatusUpdateRequest request, String requestingUserRoles) {
        log.info("Updating user status - userId: {}, enabled: {}", id, request.getEnabled());

        if (!hasRole(requestingUserRoles, "ADMIN")) {
            log.warn("Access denied - non-admin tried to update user status");
            throw new ForbiddenException("Only admins can update user status");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(request.getEnabled());
        user = userRepository.save(user);

        log.info("User status updated - userId: {}, enabled: {}", id, request.getEnabled());

        return mapToResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(String id, String requestingUserId, String requestingUserRoles) {
        log.info("Deleting user - userId: {}, requestingUserId: {}", id, requestingUserId);

        if (id.equals(requestingUserId)) {
            log.warn("User tried to delete themselves - userId: {}", id);
            throw new ForbiddenException("You cannot delete your own account");
        }

        if (!hasRole(requestingUserRoles, "ADMIN")) {
            log.warn("Access denied - non-admin tried to delete user");
            throw new ForbiddenException("Only admins can delete users");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(false);
        userRepository.save(user);

        log.info("User soft deleted - userId: {}", id);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable, String requestingUserRoles) {
        log.info("Fetching all users - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        if (!hasRole(requestingUserRoles, "ADMIN")) {
            log.warn("Access denied - non-admin tried to list all users");
            throw new ForbiddenException("Only admins can view all users");
        }

        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .image(user.getImage())
                .enabled(user.isEnabled())
                .role(user.getRole())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    private boolean hasRole(String roles, String targetRole) {
        if (roles == null || roles.isBlank()) {
            return false;
        }
        return roles.contains("ROLE_" + targetRole) || roles.contains(targetRole);
    }
}
