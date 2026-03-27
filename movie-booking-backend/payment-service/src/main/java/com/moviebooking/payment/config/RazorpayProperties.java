package com.moviebooking.payment.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class RazorpayProperties {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @PostConstruct
    public void logConfig() {
        System.out.println("=== Razorpay Config ===");
        System.out.println("keyId: " + keyId);
        System.out.println("keySecret null? " + (keySecret == null));
        System.out.println("webhookSecret: " + webhookSecret);
    }
}