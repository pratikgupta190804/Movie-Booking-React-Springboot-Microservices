package com.moviebooking.payment.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RazorpayConfig {

    private final RazorpayProperties razorpayProperties;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        log.info("Creating RazorpayClient with keyId: {}", razorpayProperties.getKeyId());
        return new RazorpayClient(
                razorpayProperties.getKeyId(),
                razorpayProperties.getKeySecret()
        );
    }
}