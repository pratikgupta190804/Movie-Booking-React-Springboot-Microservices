package com.moviebooking.userservice.config;

import com.moviebooking.userservice.exception.ResourceNotFoundException;
import com.moviebooking.userservice.model.User;
import com.moviebooking.userservice.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;


@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;
    private final KeycloakRoleConverter roleConverter = new KeycloakRoleConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String userId = jwt.getSubject();

        // Enforce that user must exist in database for non-sync endpoints
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with Id: " + userId + ". Please sync your account first."
                ));

        // Enforce that user account must be enabled
        if (!user.isEnabled()) {
            throw new DisabledException("User account is disabled");
        }

        return new JwtAuthenticationToken(
                jwt,
                roleConverter.convert(jwt),
                userId
        );
    }
}