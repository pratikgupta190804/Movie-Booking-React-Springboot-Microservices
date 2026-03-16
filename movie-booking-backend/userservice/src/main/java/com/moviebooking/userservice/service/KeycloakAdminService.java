package com.moviebooking.userservice.service;

import com.moviebooking.userservice.config.KeycloakProperties;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private static final String DEFAULT_CUSTOMER_ROLE = "CUSTOMER";

    private final KeycloakProperties keycloakProperties;
    private Keycloak keycloak;

    @PostConstruct
    public void initKeycloak() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getServerUrl())
                .realm("master") // Admin users are in master realm
                .clientId("admin-cli") // Use admin-cli for username/password auth
                .grantType(OAuth2Constants.PASSWORD)
                .username(keycloakProperties.getAdminUsername())
                .password(keycloakProperties.getAdminPassword())
                .build();
    }

    public String createUser(String username, String email, String password, String firstName, String lastName) {
        try {
            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();

            // Check if user already exists
            if (!usersResource.search(username).isEmpty()) {
                throw new RuntimeException("Username already exists");
            }
            if (!usersResource.search(null, null, null, email, null, null).isEmpty()) {
                throw new RuntimeException("Email already exists");
            }

            // Create user representation
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmailVerified(true); // Set to true to allow password authentication
            user.setRequiredActions(new java.util.ArrayList<>()); // Clear any required actions

            // Create user
            Response response = usersResource.create(user);
            
            if (response.getStatus() != 201) {
                log.error("Failed to create user. Status: {}", response.getStatus());
                throw new RuntimeException("Failed to create user in Keycloak");
            }

            // Get the user ID from the response
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("Created user in Keycloak with ID: {}", userId);

            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            UserResource userResource = usersResource.get(userId);
            userResource.resetPassword(credential);

            // Explicitly persist a login-ready state in case realm-level defaults add required actions.
            UserRepresentation createdUser = userResource.toRepresentation();
            createdUser.setEnabled(true);
            createdUser.setEmailVerified(true);
            createdUser.setRequiredActions(Collections.emptyList());
            userResource.update(createdUser);

            assignRealmRoleToUser(realmResource, userResource, DEFAULT_CUSTOMER_ROLE);

            log.info("Successfully created user in Keycloak: {} with ID: {}", username, userId);
            return userId;

        } catch (Exception e) {
            log.error("Failed to create user in Keycloak: {}", username, e);
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    public void updateUser(String userId, String name) {
        try {
            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();
            UserResource userResource = usersResource.get(userId);

            UserRepresentation user = userResource.toRepresentation();

            // Update user attributes
            if (name != null) {
                String[] names = name.split(" ", 2);
                user.setFirstName(names[0]);
                user.setLastName(names.length > 1 ? names[1] : "");
            }

            userResource.update(user);
            log.info("Successfully updated user in Keycloak: {}", userId);

        } catch (Exception e) {
            log.error("Failed to update user in Keycloak: {}", userId, e);
            throw new RuntimeException("Failed to update user in Keycloak", e);
        }
    }

    public UserRepresentation getUserById(String userId) {
        try {
            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();
            return usersResource.get(userId).toRepresentation();
        } catch (Exception e) {
            log.error("Failed to get user from Keycloak: {}", userId, e);
            throw new RuntimeException("Failed to get user from Keycloak", e);
        }
    }

    private void assignRealmRoleToUser(RealmResource realmResource, UserResource userResource, String roleName) {
        try {
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
            userResource.roles().realmLevel().add(List.of(role));
            log.info("Assigned Keycloak realm role {} to user {}", roleName, userResource.toRepresentation().getUsername());
        } catch (Exception e) {
            log.error("Failed to assign Keycloak role {} to user", roleName, e);
            throw new RuntimeException("Failed to assign Keycloak role " + roleName, e);
        }
    }
}