package com.moviebooking.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// filter/JwtAuthenticationFilter.java
@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Skip if no Authorization header
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return chain.filter(exchange);
        }

        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {
                    Jwt jwt = auth.getToken();

                    // ── Forward user info to downstream services ───────
                    // So each service knows who the user is
                    // without needing to validate the JWT again
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header("X-User-Id",    jwt.getSubject())
                            .header("X-User-Email", getClaimAsString(jwt, "email"))
                            .header("X-User-Name",  getClaimAsString(jwt, "preferred_username"))
                            .build();

                    log.debug("Forwarding request with userId: {}", jwt.getSubject());

                    return chain.filter(exchange.mutate()
                            .request(mutatedRequest)
                            .build());
                })
                .switchIfEmpty(chain.filter(exchange)); // unauthenticated — pass through
    }

    private String getClaimAsString(Jwt jwt, String claim) {
        Object value = jwt.getClaims().get(claim);
        return value != null ? value.toString() : "";
    }

    @Override
    public int getOrder() {
        return -1;  // run before other filters
    }
}
