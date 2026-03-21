package com.moviebooking.payment.client;

import com.moviebooking.payment.dtos.external.BookingResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "booking-service", url = "${booking-service.url}")
public interface BookingServiceClient {

    @GetMapping("/{bookingId}")
    BookingResponseDto getBookingById(@PathVariable String bookingId);
}
