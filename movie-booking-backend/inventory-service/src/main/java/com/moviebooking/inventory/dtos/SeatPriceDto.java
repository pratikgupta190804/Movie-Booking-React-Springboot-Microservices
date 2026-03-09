package com.moviebooking.inventory.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeatPriceDto {
    private String id;
    private String rowLabel;
    private String seatType;
    private BigDecimal price;
}
