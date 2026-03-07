package com.moviebooking.inventory.entity;

import com.moviebooking.inventory.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "show_seat_inventory",
        indexes = {
                @Index(name = "idx_show_id", columnList = "show_id"),
                @Index(name = "idx_show_seat", columnList = "show_id, seat_id"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_user_lock", columnList = "show_id, user_id, status")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"show_id", "seat_id"})
        })
public class ShowSeatInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String showId;
    private String seatId;
    private String screenId;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private String userId;
    private String bookingId;

    private LocalDateTime lockedAt;
    private LocalDateTime lockExpiresAt;

    @Version
    private Long version;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
