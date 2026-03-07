package com.moviebooking.show.dtos.kafkaDtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShowCreatedEvent {

    private String showId;

    private String screenId;

    private String theatreId;

    private String movieId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<SeatPriceEvent> seatPrices;
}
