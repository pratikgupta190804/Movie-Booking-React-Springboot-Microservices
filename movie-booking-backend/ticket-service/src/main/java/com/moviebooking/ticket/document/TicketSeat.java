package com.moviebooking.ticket.document;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketSeat {

    private String seatId;
    private String seatNumber;          // e.g. "A1", "B12"
    private String rowLabel;            // e.g. "A", "B"
    private String seatType;            // "REGULAR", "PREMIUM", "RECLINER"
    private BigDecimal price;
}
