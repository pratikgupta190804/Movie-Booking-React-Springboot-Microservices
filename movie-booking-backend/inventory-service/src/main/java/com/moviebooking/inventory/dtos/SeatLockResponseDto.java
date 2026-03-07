package com.moviebooking.inventory.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatLockResponseDto {
    private List<String> seatIds;
    private LocalDateTime lockExpiresAt;
    private String message;
    
    public SeatLockResponseDto(List<String> seatIds, LocalDateTime lockExpiresAt) {
        this.seatIds = seatIds;
        this.lockExpiresAt = lockExpiresAt;
        this.message = "Seats locked successfully";
    }
}
