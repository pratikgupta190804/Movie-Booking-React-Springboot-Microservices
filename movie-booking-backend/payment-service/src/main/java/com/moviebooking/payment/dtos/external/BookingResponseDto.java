package com.moviebooking.payment.dtos.external;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {

    private String id;
    private String userId;
    private BigDecimal finalAmount;
    private BookingStatus status;       // PENDING, CONFIRMED, CANCELLED
    private LocalDateTime expiryTime;
}