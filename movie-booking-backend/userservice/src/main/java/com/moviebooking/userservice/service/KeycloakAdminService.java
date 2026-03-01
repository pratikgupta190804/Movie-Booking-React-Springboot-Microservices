package com.moviebooking.userservice.service;

import com.moviebooking.userservice.config.KeycloakProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

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
}