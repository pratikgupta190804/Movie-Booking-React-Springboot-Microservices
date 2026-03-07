package com.moviebooking.inventory.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservedEvent {
    private String showId;
    private List<String> seatIds;
    private String userId;
    private LocalDateTime lockExpiresAt;
}
