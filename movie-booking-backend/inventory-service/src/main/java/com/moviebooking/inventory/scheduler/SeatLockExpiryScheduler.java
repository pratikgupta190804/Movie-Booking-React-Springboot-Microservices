package com.moviebooking.inventory.scheduler;

import com.moviebooking.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatLockExpiryScheduler {

    private final InventoryService inventoryService;

    @Scheduled(fixedDelay = 30000)
    public void releaseExpiredLocks() {
        log.debug("Running seat lock expiry check");
        try {
            inventoryService.releaseExpiredLocks();
        } catch (Exception e) {
            log.error("Error releasing expired locks", e);
        }
    }
}
