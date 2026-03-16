package com.moviebooking.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledEvent {
    
    private String bookingId;
    private String userId;
    private String showId;
    private List<String> seatIds;
    private String reason;
}
