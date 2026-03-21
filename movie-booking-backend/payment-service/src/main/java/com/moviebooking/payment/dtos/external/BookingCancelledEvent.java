package com.moviebooking.payment.dtos.external;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledEvent {
    private String bookingId;
    private String userId;
    private String showId;
    private List<String> seatIds;
    private String reason;
}