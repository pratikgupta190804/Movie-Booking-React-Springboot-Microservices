package com.moviebooking.booking.repo;

import com.moviebooking.booking.entity.Booking;
import com.moviebooking.booking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    
    List<Booking> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<Booking> findByUserIdAndStatus(String userId, BookingStatus status);
    
    Optional<Booking> findByIdAndUserId(String id, String userId);
    
    List<Booking> findByShowId(String showId);
    
    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.expiryTime < :currentTime")
    List<Booking> findExpiredBookings(BookingStatus status, LocalDateTime currentTime);
    
    Optional<Booking> findByBookingReference(String bookingReference);
    
    boolean existsByBookingReference(String bookingReference);
}
