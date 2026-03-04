package com.moviebooking.theatre.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ScreenLayoutRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private String screenType;

    @NotNull
    private Integer totalRows;

    @NotNull
    private Integer maxSeatsPerRow;

    @NotEmpty
    private List<SeatLayoutRequestDto> seatLayout;
}