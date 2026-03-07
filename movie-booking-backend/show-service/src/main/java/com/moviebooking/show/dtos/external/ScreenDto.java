package com.moviebooking.show.dtos.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenDto {
    private String id;
    private String name;
    private String screenType;
    private Integer totalSeats;
    private String theatreId;
}
