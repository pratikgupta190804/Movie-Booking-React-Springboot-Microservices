package com.moviebooking.inventory.listener;

import com.moviebooking.inventory.dtos.PaymentFailedEvent;
import com.moviebooking.inventory.dtos.PaymentSuccessfulEvent;
import com.moviebooking.inventory.dtos.ShowCreatedEvent;
import com.moviebooking.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "show-created-event",
            groupId = "inventory-service-v2"
    )
    public void handleShowCreated(ShowCreatedEvent event) {
        log.info("Received ShowCreatedEvent for show: {}", event.getShowId());
        try {
            inventoryService.createInventoryForShow(event);
        } catch (Exception e) {
            log.error("Error processing ShowCreatedEvent for show: {}", event.getShowId(), e);
        }
    }

    @KafkaListener(
            topics = "payment-successful-event",
            groupId = "inventory-service-v2"
            // ← no containerFactory needed
    )
    public void handlePaymentSuccess(PaymentSuccessfulEvent event) {
        log.info("Received PaymentSuccessfulEvent for booking: {}", event.getBookingId());
        try {
            inventoryService.confirmSeatsForBooking(event);
        } catch (Exception e) {
            log.error("Error processing PaymentSuccessfulEvent for booking: {}", event.getBookingId(), e);
        }
    }

    @KafkaListener(
            topics = "payment-failed-event",
            groupId = "inventory-service-v2"
    )
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Received PaymentFailedEvent for user: {}", event.getUserId());
        try {
            inventoryService.releaseSeatsAfterPaymentFailure(event);
        } catch (Exception e) {
            log.error("Error processing PaymentFailedEvent", e);
        }
    }
}