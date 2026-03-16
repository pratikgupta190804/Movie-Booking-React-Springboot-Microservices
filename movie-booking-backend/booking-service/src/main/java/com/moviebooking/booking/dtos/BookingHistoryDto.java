package com.moviebooking.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistoryDto {
    
    private String id;
    private String bookingReference;
    private String movieName;
    private String theatreName;
    private String screenName;
    private LocalDateTime showTime;
    private Integer numberOfSeats;
    private BigDecimal finalAmount;
    private String status;
    private LocalDateTime bookingDate;
}
