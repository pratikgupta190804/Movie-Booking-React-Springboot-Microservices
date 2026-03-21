package com.moviebooking.payment.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {
    private String bookingId;
    private String paymentId;
    private String reason;
    private String failureCode;
    private LocalDateTime failedAt;
}