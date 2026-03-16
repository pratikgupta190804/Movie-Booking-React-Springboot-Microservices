package com.moviebooking.booking.dtos;

import com.moviebooking.booking.enums.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeatBookingDto {
    
    @NotBlank(message = "Seat ID is required")
    private String seatId;
    
    private String seatNumber;
    private Integer rowNumber;
    private Integer seatNumberInRow;
    
    @NotNull(message = "Seat type is required")
    private SeatType seatType;
    
    @NotNull(message = "Price is required")
    private BigDecimal price;
}
