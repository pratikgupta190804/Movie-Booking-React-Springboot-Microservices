package com.moviebooking.ticket.listener;

import com.moviebooking.ticket.dtos.PaymentSuccessfulEvent;
import com.moviebooking.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// listener/TicketEventListener.java
@Component
@RequiredArgsConstructor
@Slf4j
public class TicketEventListener {

    private final TicketService ticketService;

    // ── Listen to payment-successful-event ────────────────────────────
    // When payment succeeds → generate ticket
    @KafkaListener(
            topics = "payment-successful-event",
            groupId = "ticket-service"
    )
    public void handlePaymentSuccess(PaymentSuccessfulEvent event) {
        log.info("Received PaymentSuccessfulEvent for bookingId: {}",
                event.getBookingId());

        try {
            ticketService.generateTicket(event);
            log.info("Ticket generated successfully for bookingId: {}",
                    event.getBookingId());
        } catch (Exception e) {
            log.error("Failed to generate ticket for bookingId: {}. Error: {}",
                    event.getBookingId(), e.getMessage(), e);
        }
    }
}
