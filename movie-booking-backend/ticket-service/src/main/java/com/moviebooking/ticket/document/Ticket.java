package com.moviebooking.ticket.document;

import com.moviebooking.ticket.enums.TicketStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// document/Ticket.java
@Document(collection = "tickets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    private String id;                          // MongoDB ObjectId

    // ── Booking info ───────────────────────────────────────────────────
    @Indexed(unique = true)
    private String bookingId;                   // from booking-service

    @Indexed
    private String userId;

    private String bookingReference;            // e.g. BK12345678
    private String showId;
    // ── Display info (denormalized — stored for fast retrieval) ────────
    // We store these here so we don't need to call other services
    // every time a user views their ticket
    private String movieName;
    private String moviePosterUrl;
    private String theatreName;
    private String theatreAddress;
    private String screenName;
    private LocalDateTime showTime;
    private String language;
    private String format;                      // "2D", "3D", "IMAX"

    // ── Seat info ──────────────────────────────────────────────────────
    private List<TicketSeat> seats;

    // ── Payment info ───────────────────────────────────────────────────
    private String paymentId;
    private BigDecimal totalAmount;

    // ── QR Code ───────────────────────────────────────────────────────
    private String qrCode;                      // base64 encoded QR image

    // ── Status ────────────────────────────────────────────────────────
    private TicketStatus status;

    // ── Timestamps ────────────────────────────────────────────────────

    private LocalDateTime generatedAt;
    private LocalDateTime updatedAt;
}
