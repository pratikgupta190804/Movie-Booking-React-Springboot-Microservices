package com.moviebooking.ticket.dtos;

import lombok.*;

import java.math.BigDecimal;

// dtos/TicketSeatDto.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSeatDto {
    private String seatId;
    private String seatNumber;
    private String rowLabel;
    private String seatType;
    private BigDecimal price;
}