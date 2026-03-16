package com.moviebooking.booking.scheduler;

import com.moviebooking.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingExpiryScheduler {

    private final BookingService bookingService;

    // Run every 1 minute to check for expired bookings
    @Scheduled(fixedRate = 60000)
    public void expireBookings() {
        log.info("Starting booking expiry check");
        try {
            bookingService.expireBookingsScheduler();
        } catch (Exception e) {
            log.error("Error during booking expiry check", e);
        }
    }
}
