package com.moviebooking.theatre.dtos;

import com.moviebooking.theatre.entity.SeatType;
import lombok.Data;

import java.util.List;

@Data
public class SeatLayoutRequestDto {
    private String rowLabel;
    private Integer startSeatNumber;
    private Integer endSeatNumber;
    private SeatType seatType;
    private Integer displayRow;
    private Integer displayColumn;
    private List<Integer> skipSeats;
}
