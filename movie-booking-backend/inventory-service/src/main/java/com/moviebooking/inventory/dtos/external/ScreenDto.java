package com.moviebooking.inventory.dtos.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenDto {
    private String id;
    private String name;
    private String screenType;
    private Integer totalSeats;
    private String theatreId;
    private List<SeatDto> seats;
}
