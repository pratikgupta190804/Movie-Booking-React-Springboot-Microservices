package com.moviebooking.payment.dtos.external;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {

    private String id;
    private String userId;
    private String userEmail;
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
    private BookingStatus status;
    private List<BookingSeatDto> seats;
}