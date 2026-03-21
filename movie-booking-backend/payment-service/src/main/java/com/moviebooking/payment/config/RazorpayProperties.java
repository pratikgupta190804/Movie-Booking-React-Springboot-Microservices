package com.moviebooking.payment.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "razorpay")
@Getter
@Setter
public class RazorpayProperties {

    private Key key = new Key();
    private String webhookSecret;

    @Getter
    @Setter
    public static class Key {
        private String id;
        private String secret;
    }

    public String getKeyId() {
        return key.getId();
    }

    public String getKeySecret() {
        return key.getSecret();
    }

    // ── Add this to verify on startup ─────────────────────────────
    @PostConstruct
    public void logConfig() {
        System.out.println("=== Razorpay Config ===");
        System.out.println("keyId: " + key.getId());
        System.out.println("keySecret null? " + (key.getSecret() == null));
        System.out.println("webhookSecret: " + webhookSecret);
    }
}