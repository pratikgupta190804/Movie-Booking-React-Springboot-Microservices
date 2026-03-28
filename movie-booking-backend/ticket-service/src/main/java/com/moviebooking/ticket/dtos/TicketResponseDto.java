package com.moviebooking.ticket.dtos;

import com.moviebooking.ticket.enums.TicketStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// dtos/TicketResponseDto.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponseDto {
    private String id;
    private String bookingId;
    private String userId;
    private String bookingReference;
    private String movieName;
    private String moviePosterUrl;
    private String theatreName;
    private String theatreAddress;
    private String screenName;
    private LocalDateTime showTime;
    private String language;
    private String format;
    private List<TicketSeatDto> seats;
    private String paymentId;
    private BigDecimal totalAmount;
    private String qrCode;              // base64 QR image
    private TicketStatus status;
    private LocalDateTime generatedAt;
}