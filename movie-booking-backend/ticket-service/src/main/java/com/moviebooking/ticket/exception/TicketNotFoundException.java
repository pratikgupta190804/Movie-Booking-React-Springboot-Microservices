package com.moviebooking.ticket.exception;

// exception/TicketNotFoundException.java
public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String message) {
        super(message);
    }
}
