package com.moviebooking.ticket.service;

import com.moviebooking.ticket.dtos.PaymentSuccessfulEvent;
import com.moviebooking.ticket.dtos.TicketResponseDto;

import java.util.List;

public interface TicketService {

    void generateTicket(PaymentSuccessfulEvent event);

    // Called by controller
    TicketResponseDto getTicketByBookingId(String bookingId, String userId);

    // service/TicketService.java — add this method
    TicketResponseDto getTicketById(String ticketId, String userId);

    List<TicketResponseDto> getUserTickets(String userId);

    // Called when booking is cancelled
    void cancelTicket(String bookingId);
}
