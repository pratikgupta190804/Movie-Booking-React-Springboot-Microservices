package com.moviebooking.booking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moviebooking.booking.enums.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking_seats",
        indexes = {
                @Index(name = "idx_booking_id", columnList = "booking_id"),
                @Index(name = "idx_seat_id", columnList = "seatId")
        })
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private Booking booking;

    @Column(nullable = false)
    private String seatId;

    private String seatNumber;
    private Integer rowNumber;
    private Integer seatNumberInRow;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
