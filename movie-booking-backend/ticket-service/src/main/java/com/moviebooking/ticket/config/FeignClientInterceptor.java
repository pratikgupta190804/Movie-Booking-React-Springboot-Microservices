package com.moviebooking.ticket.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate template) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

                if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_TOKEN_TYPE)) {
                    template.header(AUTHORIZATION_HEADER, authorizationHeader);
                }
            }
            // If no request context (e.g., Kafka listener), proceed without authorization header
            // Target services should allow internal GET endpoints without authentication
        } catch (Exception e) {
            // Silently continue if we can't get request context
            // This happens in non-HTTP contexts like Kafka listeners
        }
    }
}
