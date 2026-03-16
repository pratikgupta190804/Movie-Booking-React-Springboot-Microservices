package com.moviebooking.booking.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateBookingRequestDto {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Show ID is required")
    private String showId;
    
    @NotEmpty(message = "At least one seat must be selected")
    @Valid
    private List<SeatBookingDto> seats;
    
    private String idempotencyKey;
}
