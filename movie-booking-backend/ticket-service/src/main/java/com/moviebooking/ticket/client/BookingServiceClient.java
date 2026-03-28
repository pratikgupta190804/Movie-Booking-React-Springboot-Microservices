package com.moviebooking.ticket.client;

import com.moviebooking.ticket.dtos.external.BookingResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "booking-service",
        url = "${booking-service.url}"
)
public interface BookingServiceClient {

    @GetMapping("/api/bookings/{bookingId}")
    BookingResponseDto getBookingById(@PathVariable("bookingId") String bookingId);
}