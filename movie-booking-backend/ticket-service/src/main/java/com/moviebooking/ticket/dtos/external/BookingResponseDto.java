package com.moviebooking.ticket.dtos.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// dtos/external/BookingResponseDto.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingResponseDto {
    private String id;
    private String userId;
    private String showId;
    private String movieId;
    private String theatreId;
    private String screenId;
    private String movieName;
    private String theatreName;
    private String screenName;
    private LocalDateTime showTime;
    private String bookingReference;
    private BigDecimal totalAmount;
    private BigDecimal convenienceFee;
    private BigDecimal totalTax;
    private BigDecimal finalAmount;
    private String status;
    private List<BookingSeatDto> seats;
}