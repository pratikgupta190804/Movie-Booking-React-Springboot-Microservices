package com.moviebooking.booking.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SeatLockResponseDto {
    private String lockId;
    private List<String> lockedSeatIds;
    private LocalDateTime lockExpiresAt;
    private String message;
}
