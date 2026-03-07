package com.moviebooking.show.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateShowRequestDto {

    @NotNull
    private String movieId;

    @NotNull
    private String theatreId;

    @NotNull
    private String screenId;

    @NotNull
    private String language;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private BigDecimal price;

    private List<SeatPriceRequestDto> seatPrices;

}