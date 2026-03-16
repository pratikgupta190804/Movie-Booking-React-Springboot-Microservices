package com.moviebooking.booking.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SeatLockRequestDto {
    private String showId;
    private String userId;
    private List<String> seatIds;
    private String idempotencyKey;
}
