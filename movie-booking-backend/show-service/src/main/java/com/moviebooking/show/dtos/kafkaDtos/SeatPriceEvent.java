package com.moviebooking.show.dtos.kafkaDtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeatPriceEvent {

    private String rowLabel;

    private String seatType;

    private BigDecimal price;
}
