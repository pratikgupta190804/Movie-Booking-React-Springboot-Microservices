package com.moviebooking.show.dtos;

import com.moviebooking.show.entity.SeatType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeatPriceRequestDto {

    private String rowLabel;
    private SeatType seatType;
    private BigDecimal price;

}