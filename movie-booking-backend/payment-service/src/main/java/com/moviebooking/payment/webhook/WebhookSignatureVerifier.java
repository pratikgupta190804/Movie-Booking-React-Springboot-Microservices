package com.moviebooking.payment.webhook;

import com.moviebooking.payment.config.RazorpayProperties;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookSignatureVerifier {

    private final RazorpayProperties razorpayProperties;

    public boolean verify(String payload, String razorpaySignature) {
        try {
            // Razorpay signs webhook body with HMAC-SHA256
            // using your webhook secret (different from key secret)
            String generatedSignature = Utils.getHash(
                    payload,
                    razorpayProperties.getWebhookSecret()
            );

            boolean isValid = generatedSignature.equals(razorpaySignature);

            if (!isValid) {
                log.warn("Webhook signature mismatch. Expected: {}, Got: {}",
                        generatedSignature, razorpaySignature);
            }

            return isValid;

        } catch (Exception e) {
            log.error("Error verifying webhook signature: {}", e.getMessage(), e);
            return false;
        }
    }
}
