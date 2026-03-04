package com.moviebooking.theatre.dtos;

import com.moviebooking.theatre.entity.SeatType;
import lombok.Data;

@Data
public class SeatDto {
    private String id;
    private String rowLabel;
    private Integer seatNumber;
    private SeatType seatType;
    private Boolean active;
    private Integer displayRow;
    private Integer displayColumn;
}