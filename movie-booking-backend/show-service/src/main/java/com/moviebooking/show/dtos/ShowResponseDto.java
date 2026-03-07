package com.moviebooking.show.dtos;

import com.moviebooking.show.entity.ShowStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShowResponseDto {

    private String id;

    private String movieId;

    private String theatreId;

    private String screenId;

    private String language;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal price;

    private List<SeatPriceResponseDto> seatPrices;

    private ShowStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}