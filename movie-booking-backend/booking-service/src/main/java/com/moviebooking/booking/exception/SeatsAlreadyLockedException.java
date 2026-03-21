package com.moviebooking.booking.exception;

public class SeatsAlreadyLockedException extends RuntimeException {
    public SeatsAlreadyLockedException(String message) {
        super(message);
    }
}
