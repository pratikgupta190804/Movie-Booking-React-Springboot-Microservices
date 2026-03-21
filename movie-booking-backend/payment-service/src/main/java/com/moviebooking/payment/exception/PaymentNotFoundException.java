package com.moviebooking.payment.exception;

// PaymentNotFoundException.java
public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}