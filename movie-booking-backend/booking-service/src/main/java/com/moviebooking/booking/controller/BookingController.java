package com.moviebooking.booking.controller;

import com.moviebooking.booking.dtos.BookingHistoryDto;
import com.moviebooking.booking.dtos.BookingResponseDto;
import com.moviebooking.booking.dtos.CreateBookingRequestDto;
import com.moviebooking.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody CreateBookingRequestDto request) {
        log.info("Request to create booking for user: {}", request.getUserId());
        BookingResponseDto response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<BookingResponseDto> getBookingById(
            @PathVariable String bookingId,
            @RequestParam String userId) {
        log.info("Request to get booking: {} for user: {}", bookingId, userId);
        BookingResponseDto response = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<BookingHistoryDto>> getUserBookingHistory(@PathVariable String userId) {
        log.info("Request to get booking history for user: {}", userId);
        List<BookingHistoryDto> history = bookingService.getUserBookingHistory(userId);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<BookingResponseDto> cancelBooking(
            @PathVariable String bookingId,
            @RequestParam String userId) {
        log.info("Request to cancel booking: {} for user: {}", bookingId, userId);
        BookingResponseDto response = bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Booking Service is running");
    }
}
