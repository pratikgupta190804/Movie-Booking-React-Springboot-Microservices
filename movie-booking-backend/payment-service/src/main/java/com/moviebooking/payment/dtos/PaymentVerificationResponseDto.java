package com.moviebooking.payment.dtos;

import com.moviebooking.payment.enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerificationResponseDto {

    private String paymentId;
    private String bookingId;
    private boolean success;
    private PaymentStatus status;
    private String message;
}