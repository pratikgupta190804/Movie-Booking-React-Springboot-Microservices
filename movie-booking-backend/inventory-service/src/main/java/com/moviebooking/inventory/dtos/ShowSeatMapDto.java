package com.moviebooking.inventory.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeatMapDto {
    private String showId;
    private String screenId;
    private String screenName;
    private Integer totalSeats;
    private Integer availableSeats;
    private List<SeatInventoryDto> seats;
}
