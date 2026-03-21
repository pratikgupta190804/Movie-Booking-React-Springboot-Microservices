package com.moviebooking.payment.listener;

import com.moviebooking.payment.dtos.RefundRequestDto;
import com.moviebooking.payment.dtos.external.BookingCancelledEvent;
import com.moviebooking.payment.entity.Payment;
import com.moviebooking.payment.repo.PaymentRepository;
import com.moviebooking.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentKafkaConsumer {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "booking-cancelled-event",
            groupId = "payment-service-group"
    )
    public void handleBookingCancelledEvent(BookingCancelledEvent event) {
        log.info("Received booking-cancelled-event for bookingId: {}", event.getBookingId());

        // Find payment for this booking
        Payment payment = paymentRepository
                .findByBookingId(event.getBookingId())
                .orElse(null);

        if (payment == null) {
            log.info("No payment found for booking: {} — nothing to refund", event.getBookingId());
            return;
        }

        // Only refund if payment was actually successful
        if (!payment.isRefundable()) {
            log.info("Payment {} is not refundable (status: {}) — skipping",
                    payment.getId(), payment.getStatus());
            return;
        }

        log.info("Auto-initiating refund for cancelled booking: {}, payment: {}",
                event.getBookingId(), payment.getId());

        // Build refund request
        RefundRequestDto refundRequest = RefundRequestDto.builder()
                .paymentId(payment.getId())
                .amount(payment.getRefundableAmount())
                .reason("BOOKING_CANCELLED: " + event.getReason())
                .build();

        // Use SYSTEM as userId for auto-refunds
        paymentService.refundPayment(refundRequest, payment.getUserId());

        log.info("Auto-refund initiated for booking: {}", event.getBookingId());
    }
}