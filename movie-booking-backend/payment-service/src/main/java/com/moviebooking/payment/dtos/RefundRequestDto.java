package com.moviebooking.payment.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequestDto {

    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Refund amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Reason is required")
    private String reason;
}