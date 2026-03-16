package com.moviebooking.booking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {
    
    private String bookingId;
    private String userId;
    private String showId;
    private String reason;
}
