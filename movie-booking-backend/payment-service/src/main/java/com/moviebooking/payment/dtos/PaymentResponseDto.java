package com.moviebooking.payment.dtos;

import com.moviebooking.payment.enums.PaymentMethod;
import com.moviebooking.payment.enums.PaymentProvider;
import com.moviebooking.payment.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

    private String id;
    private String userId;
    private String bookingId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod method;
    private PaymentProvider provider;
    private String providerOrderId;
    private String providerPaymentId;
    private BigDecimal refundedAmount;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private List<RefundResponseDto> refunds;
}