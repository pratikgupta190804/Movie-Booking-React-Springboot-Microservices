package com.moviebooking.inventory.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SeatLockRequestDto {
    
    @NotBlank(message = "Show ID is required")
    private String showId;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotEmpty(message = "At least one seat must be selected")
    private List<String> seatIds;
    
    private String idempotencyKey;
}
