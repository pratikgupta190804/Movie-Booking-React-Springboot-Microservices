package com.moviebooking.booking.service;

import com.moviebooking.booking.dtos.BookingHistoryDto;
import com.moviebooking.booking.dtos.BookingResponseDto;
import com.moviebooking.booking.dtos.CreateBookingRequestDto;
import com.moviebooking.booking.dtos.PaymentFailedEvent;
import com.moviebooking.booking.dtos.PaymentSuccessfulEvent;

import java.util.List;

public interface BookingService {
    
    BookingResponseDto createBooking(CreateBookingRequestDto request);
    
    BookingResponseDto getBookingById(String bookingId, String userId);
    
    List<BookingHistoryDto> getUserBookingHistory(String userId);
    
    BookingResponseDto cancelBooking(String bookingId, String userId);
    
    void handlePaymentSuccess(PaymentSuccessfulEvent event);
    
    void handlePaymentFailed(PaymentFailedEvent event);
    
    void expireBookingsScheduler();
}
