package com.moviebooking.booking.enums;

public enum BookingStatus {
    PENDING,           // Initial state when booking is created
    PAYMENT_INITIATED, // Payment gateway initiated
    CONFIRMED,         // Payment successful, seats confirmed
    CANCELLED,         // User cancelled the booking
    EXPIRED,          // Booking expired due to timeout
    REFUNDED          // Payment refunded after cancellation
}
