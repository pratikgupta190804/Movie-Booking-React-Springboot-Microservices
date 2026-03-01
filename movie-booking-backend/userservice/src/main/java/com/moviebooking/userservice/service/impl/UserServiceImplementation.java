package com.moviebooking.userservice.service.impl;

import com.moviebooking.userservice.dtos.*;
import com.moviebooking.userservice.model.Role;
import com.moviebooking.userservice.model.User;
import com.moviebooking.userservice.exception.ForbiddenOperationException;
import com.moviebooking.userservice.exception.ResourceNotFoundException;
import com.moviebooking.userservice.repo.UserRepository;
import com.moviebooking.userservice.service.KeycloakAdminService;
import com.moviebooking.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    @Override
    public UserResponse syncUser(SyncUserRequest request) {

        log.info("Syncing user with ID: {}", request.getId());

        User user = getUserEntity(request.getId());
        user.setEnabled(true);
        user.setEmail(request.getEmail());
        user.setUserName(request.getUserName());
        user.setName(request.getName());
        user.setImage(request.getImage());
        user.setProvider(request.getProvider());
        user.setProviderId(request.getProviderId());
        try {
            user.setRole(Role.valueOf(request.getRole()));
        } catch (IllegalArgumentException e) {
            log.warn("Unknown role '{}' provided, defaulting to CUSTOMER", request.getRole());
            user.setRole(Role.CUSTOMER);
        }

        userRepository.save(user);

        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentMe(String loggedInUser) {

        User user = getUserEntity(loggedInUser);

        log.info("Getting Current user info {}", user.getEmail());

        if (!user.isEnabled()) {
            throw new ForbiddenOperationException("Account is disabled");
        }

        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(String requestedUserId) {

        User user = getUserEntity(requestedUserId);

        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUser(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public UserResponse updateProfile(UserUpdateDto updateDto, String loggedInUser) {
        log.info("Updating user with ID: {}", loggedInUser);
        User user = getUserEntity(loggedInUser);

        try {
            keycloakAdminService.updateUser(
                    loggedInUser,
                    updateDto.getName()
            );
            log.info("User updated in Keycloak successfully");
        } catch (Exception e) {
            log.error("Failed to update user in Keycloak", e);
            throw new RuntimeException("Failed to update user in Keycloak: " + e.getMessage());
        }

        if (updateDto.getUserName() != null) {
            user.setUserName(updateDto.getUserName());
        }
        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
        }
        if (updateDto.getImage() != null) {
            user.setImage(updateDto.getImage());
        }
        User savedUser = userRepository.save(user);

        log.info("User {} updated profile", loggedInUser);

        return mapToResponse(savedUser);
    }

    @Override
    public void disableProfile(String requestedUserId, String loggedInUser) {

        User currentUser = getUserEntity(loggedInUser);

        boolean isAdmin = currentUser.getRole() != null && currentUser.getRole() == Role.ADMIN;

        if (!requestedUserId.equals(loggedInUser) && !isAdmin) {
            throw new ForbiddenOperationException("Not authorized to delete this account");
        }

        User targetUser = requestedUserId.equals(loggedInUser)
                ? currentUser
                : getUserEntity(requestedUserId);

        if (!targetUser.isEnabled()) {
            throw new ForbiddenOperationException("Account already disabled");
        }

        targetUser.setEnabled(false);

        userRepository.save(targetUser);

        log.info("User {} disabled account of {}", loggedInUser, requestedUserId);
    }

    private User getUserEntity(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .name(user.getName())
                .image(user.getImage())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .provider(user.getProvider() != null ? user.getProvider().name() : null)
                .providerId(user.getProviderId())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .build();
    }
}