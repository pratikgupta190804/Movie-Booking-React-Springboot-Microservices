package com.moviebooking.theatre.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ScreenResponseDto {

    private String id;
    private String name;
    private String screenType;
    private Integer totalRows;
    private Integer maxSeatsPerRow;
    private Integer totalSeats;
    private String theatreId;
    private String theatreName;
    private List<SeatDto> seats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
