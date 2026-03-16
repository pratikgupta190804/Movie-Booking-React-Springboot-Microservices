package com.moviebooking.booking.dtos.external;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShowDto {
    private String id;
    private String movieId;
    private String screenId;
    private String theatreId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
