package com.moviebooking.ticket.repository;

import com.moviebooking.ticket.document.Ticket;
import com.moviebooking.ticket.enums.TicketStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// repository/TicketRepository.java
@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

    // Find ticket by bookingId — most common query
    Optional<Ticket> findByBookingId(String bookingId);

    // Find all tickets for a user — for ticket history
    List<Ticket> findByUserIdOrderByGeneratedAtDesc(String userId);

    // Check if ticket already exists for a booking — idempotency
    boolean existsByBookingId(String bookingId);

    // Find by userId and status — e.g. all active tickets
    List<Ticket> findByUserIdAndStatus(String userId, TicketStatus status);

    // Find by showId — useful for admin/venue scanning
    List<Ticket> findByShowId(String showId);
}
