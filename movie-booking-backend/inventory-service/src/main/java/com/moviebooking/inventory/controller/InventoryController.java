package com.moviebooking.inventory.controller;

import com.moviebooking.inventory.dtos.SeatLockRequestDto;
import com.moviebooking.inventory.dtos.SeatLockResponseDto;
import com.moviebooking.inventory.dtos.ShowSeatMapDto;
import com.moviebooking.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/shows/{showId}/seats")
    public ResponseEntity<ShowSeatMapDto> getSeatMapForShow(@PathVariable String showId) {
        log.info("Request to get seat map for show: {}", showId);
        ShowSeatMapDto seatMap = inventoryService.getSeatMapForShow(showId);
        return ResponseEntity.ok(seatMap);
    }

    @PostMapping("/seats/lock")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<SeatLockResponseDto> lockSeats(@Valid @RequestBody SeatLockRequestDto request) {
        log.info("Request to lock seats for user: {} in show: {}", request.getUserId(), request.getShowId());
        SeatLockResponseDto response = inventoryService.lockSeats(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Inventory Service is running");
    }
}
