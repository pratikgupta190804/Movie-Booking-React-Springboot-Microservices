package com.moviebooking.payment.dtos;

import com.moviebooking.payment.enums.RefundStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponseDto {

    private String refundId;
    private String paymentId;
    private BigDecimal amount;
    private RefundStatus status;
    private String providerRefundId;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}