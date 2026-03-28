package com.moviebooking.payment.dtos;

import com.moviebooking.payment.dtos.external.BookingSeatDto;
import com.moviebooking.payment.dtos.external.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessfulEvent {
    private String bookingId;
    private String paymentId;
    private String userId;              // ← add
    private String showId;              // ← add
    private String providerPaymentId;
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
    private LocalDateTime paidAt;
}