package com.moviebooking.booking.client;

import com.moviebooking.booking.dtos.SeatLockRequestDto;
import com.moviebooking.booking.dtos.SeatLockResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", url = "${inventory-service.url:http://localhost:8085}")
public interface InventoryServiceClient {

    @PostMapping("/api/inventory/seats/lock")
    SeatLockResponseDto lockSeats(@RequestBody SeatLockRequestDto request);
}
