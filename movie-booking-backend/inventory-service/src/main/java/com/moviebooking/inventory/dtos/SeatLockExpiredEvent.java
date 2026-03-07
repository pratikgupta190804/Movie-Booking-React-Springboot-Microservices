package com.moviebooking.inventory.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatLockExpiredEvent {
    private String showId;
    private List<String> seatIds;
}
