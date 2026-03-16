package com.moviebooking.booking.dtos;

import com.moviebooking.booking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    
    private String id;
    private String bookingReference;
    private String userId;
    private String showId;
    private String movieId;
    private String movieName;
    private String theatreName;
    private String screenName;
    private LocalDateTime showTime;
    private List<SeatBookingDto> seats;
    private BigDecimal totalAmount;
    private BigDecimal convenienceFee;
    private BigDecimal totalTax;
    private BigDecimal finalAmount;
    private BookingStatus status;
    private String paymentId;
    private String transactionId;
    private LocalDateTime bookingDate;
    private LocalDateTime expiryTime;
    private String message;
}
