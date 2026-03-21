package com.moviebooking.payment.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ── CORS — must be applied here, not just as a Bean ───────
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // ── CSRF — disable fully for stateless REST API ────────────
                // JWT-based APIs don't use sessions so CSRF is not needed
                .csrf(csrf -> csrf.disable())

                // ── Stateless session — no HttpSession created ─────────────
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ── Authorization ──────────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/payments/webhook").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // ← preflight
                        .anyRequest().authenticated()
                )

                // ── JWT resource server ────────────────────────────────────
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }
}