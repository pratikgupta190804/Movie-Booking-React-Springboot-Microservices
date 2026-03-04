package com.moviebooking.theatre.entity;

import java.time.LocalDateTime;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seats",
        indexes = {
                @Index(name = "idx_screen_id", columnList = "screen_id"),
                @Index(name = "idx_row_seat", columnList = "row_label, seat_number")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"screen_id", "row_label", "seat_number"})
        })
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String rowLabel;
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private Boolean active = true;

    private Integer displayRow;
    private Integer displayColumn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @CreationTimestamp
    private LocalDateTime createdAt;
}