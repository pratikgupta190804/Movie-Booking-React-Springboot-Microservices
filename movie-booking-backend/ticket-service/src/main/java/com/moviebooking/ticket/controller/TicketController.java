package com.moviebooking.ticket.controller;

import com.moviebooking.ticket.dtos.TicketResponseDto;
import com.moviebooking.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// controller/TicketController.java
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    // ── Get ticket by bookingId ────────────────────────────────────────
    // User fetches their ticket after payment
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<TicketResponseDto> getTicketByBookingId(
            @PathVariable String bookingId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("GET /api/tickets/booking/{} — userId: {}", bookingId, userId);

        TicketResponseDto ticket = ticketService.getTicketByBookingId(bookingId, userId);
        return ResponseEntity.ok(ticket);
    }

    // ── Get all tickets for logged in user ─────────────────────────────
    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketResponseDto>> getUserTickets(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("GET /api/tickets/my-tickets — userId: {}", userId);

        List<TicketResponseDto> tickets = ticketService.getUserTickets(userId);
        return ResponseEntity.ok(tickets);
    }

    // ── Get ticket by ticketId ─────────────────────────────────────────
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponseDto> getTicketById(
            @PathVariable String ticketId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("GET /api/tickets/{} — userId: {}", ticketId, userId);

        // Fetch by ticketId — find by id then verify ownership
        TicketResponseDto ticket = ticketService.getTicketById(ticketId, userId);
        return ResponseEntity.ok(ticket);
    }
}