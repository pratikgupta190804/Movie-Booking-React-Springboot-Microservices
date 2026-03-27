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
    private String showId;              // ← add
    private BigDecimal finalAmount;
    private BigDecimal totalAmount;
    private BigDecimal convenienceFee;
    private BigDecimal totalTax;
    private BookingStatus status;
    private LocalDateTime expiryTime;
    private String bookingReference;
}