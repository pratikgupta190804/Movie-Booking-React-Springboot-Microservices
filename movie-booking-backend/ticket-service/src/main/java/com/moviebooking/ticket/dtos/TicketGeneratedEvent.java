package com.moviebooking.ticket.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// dtos/TicketGeneratedEvent.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketGeneratedEvent {
    private String ticketId;
    private String bookingId;
    private String userId;
    private String userEmail;           // notification-service needs this to send email
    private String bookingReference;
    private String movieName;
    private String theatreName;
    private String screenName;
    private LocalDateTime showTime;
    private BigDecimal totalAmount;
    private String qrCode;              // base64 — attached in email
    private LocalDateTime generatedAt;
}