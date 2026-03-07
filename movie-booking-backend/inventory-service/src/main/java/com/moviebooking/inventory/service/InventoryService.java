package com.moviebooking.inventory.service;

import com.moviebooking.inventory.dtos.*;

public interface InventoryService {
    

    void createInventoryForShow(ShowCreatedEvent event);

    ShowSeatMapDto getSeatMapForShow(String showId);

    SeatLockResponseDto lockSeats(SeatLockRequestDto request);

    void confirmSeatsForBooking(PaymentSuccessfulEvent event);

    void releaseSeatsAfterPaymentFailure(PaymentFailedEvent event);

    void releaseExpiredLocks();
}
