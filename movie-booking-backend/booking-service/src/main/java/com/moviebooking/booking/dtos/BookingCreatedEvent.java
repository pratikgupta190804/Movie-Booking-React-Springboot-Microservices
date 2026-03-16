package com.moviebooking.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {
    
    private String bookingId;
    private String userId;
    private String showId;
    private List<String> seatIds;
}
