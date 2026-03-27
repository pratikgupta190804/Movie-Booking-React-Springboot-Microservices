package com.moviebooking.payment.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessEvent {
    private String bookingId;
    private String paymentId;
    private String userId;              // ← add
    private String showId;              // ← add
    private String providerPaymentId;
    private BigDecimal amount;
    private LocalDateTime paidAt;
}