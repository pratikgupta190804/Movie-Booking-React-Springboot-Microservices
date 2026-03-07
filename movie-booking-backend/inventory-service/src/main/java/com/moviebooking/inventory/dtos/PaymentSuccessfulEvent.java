package com.moviebooking.inventory.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessfulEvent {
    private String bookingId;
    private String userId;
    private String showId;
}
