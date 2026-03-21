package com.moviebooking.payment.dtos;

import com.moviebooking.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderResponseDto {

    private String paymentId;
    private String bookingId;

    private String razorpayOrderId;
    private String razorpayKeyId;

    private BigDecimal amount;
    private String currency;

    private PaymentStatus status;
    private String message;
}