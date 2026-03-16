package com.moviebooking.booking.entity;

import com.moviebooking.booking.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings",
        indexes = {
                @Index(name = "idx_user_id", columnList = "userId"),
                @Index(name = "idx_show_id", columnList = "showId"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_booking_date", columnList = "bookingDate"),
                @Index(name = "idx_user_status", columnList = "userId, status")
        })
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String showId;

    @Column(nullable = false)
    private String movieId;

    @Column(nullable = false)
    private String theatreId;

    @Column(nullable = false)
    private String screenId;

    private String movieName;
    private String theatreName;
    private String screenName;

    private LocalDateTime showTime;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BookingSeat> seats = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal convenienceFee;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalTax;

    @Column(precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    private String paymentId;          // Reference to payment service
    private String transactionId;       // Transaction ID from payment gateway

    private LocalDateTime bookingDate;
    private LocalDateTime expiryTime;   // When booking expires if not paid

    @Column(unique = true)
    private String bookingReference;    // Unique booking number (e.g., BK123456)

    @Version
    private Long version;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper method to add seats
    public void addSeat(BookingSeat seat) {
        seats.add(seat);
        seat.setBooking(this);
    }

    // Helper method to calculate totals
    public void calculateTotals() {
        this.totalAmount = seats.stream()
                .map(BookingSeat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate convenience fee (2% of ticket price)
        this.convenienceFee = totalAmount.multiply(BigDecimal.valueOf(0.02));
        
        // Calculate tax (18% GST)
        this.totalTax = totalAmount.add(convenienceFee).multiply(BigDecimal.valueOf(0.18));
        
        // Final amount
        this.finalAmount = totalAmount.add(convenienceFee).add(totalTax);
    }
}
