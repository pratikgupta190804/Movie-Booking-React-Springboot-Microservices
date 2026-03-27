package com.moviebooking.booking.listener;

import com.moviebooking.booking.dtos.PaymentFailedEvent;
import com.moviebooking.booking.dtos.PaymentSuccessfulEvent;
import com.moviebooking.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventListener {

    private final BookingService bookingService;

    @KafkaListener(topics = "payment-successful-event", groupId = "booking-service-v2")
    public void handlePaymentSuccess(PaymentSuccessfulEvent event) {
        log.info("Received PaymentSuccessfulEvent for booking: {}", event.getBookingId());
        try {
            bookingService.handlePaymentSuccess(event);
            log.info("Successfully processed PaymentSuccessfulEvent for booking: {}", event.getBookingId());
        } catch (Exception e) {
            log.error("Error processing PaymentSuccessfulEvent for booking: {}", event.getBookingId(), e);
            // In production, consider implementing retry logic or dead letter queue
        }
    }

    @KafkaListener(topics = "payment-failed-event", groupId = "booking-service-v2")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Received PaymentFailedEvent for booking: {}", event.getBookingId());
        try {
            bookingService.handlePaymentFailed(event);
            log.info("Successfully processed PaymentFailedEvent for booking: {}", event.getBookingId());
        } catch (Exception e) {
            log.error("Error processing PaymentFailedEvent for booking: {}", event.getBookingId(), e);
        }
    }
}
