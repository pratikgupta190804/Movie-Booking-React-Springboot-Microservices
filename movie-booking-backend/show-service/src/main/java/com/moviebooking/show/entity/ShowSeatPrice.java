package com.moviebooking.show.entity;

import jakarta.persistence.*;
        import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "show_seat_prices")
public class ShowSeatPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String rowLabel;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "show_id")
    private Show show;

}