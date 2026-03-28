package com.moviebooking.ticket.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moviebooking.ticket.dtos.external.BookingSeatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// dtos/PaymentSuccessfulEvent.java
// This is what payment-service publishes to Kafka
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSuccessfulEvent {
    private String bookingId;
    private String paymentId;
    private String userId;
    private String showId;
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
    private String status;
    private List<BookingSeatDto> seats;
    private LocalDateTime paidAt;
}