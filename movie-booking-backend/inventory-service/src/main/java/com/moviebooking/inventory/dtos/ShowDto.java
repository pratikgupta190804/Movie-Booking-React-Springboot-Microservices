package com.moviebooking.inventory.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShowDto {
    private String id;
    private String movieId;
    private String screenId;
    private String theatreId;
    private String language;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private List<SeatPriceDto> seatPrices;
    private String status;
}
