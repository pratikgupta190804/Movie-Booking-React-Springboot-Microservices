package com.moviebooking.theatre.dtos;

import lombok.Data;

@Data
public class ScreenSummaryDto {

    private String id;
    private String name;
    private String screenType;
    private Integer totalSeats;
}
