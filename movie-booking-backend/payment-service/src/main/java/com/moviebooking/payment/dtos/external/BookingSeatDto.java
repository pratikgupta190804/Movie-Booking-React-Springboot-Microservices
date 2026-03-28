package com.moviebooking.payment.dtos.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;

// dtos/external/BookingSeatDto.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingSeatDto {
    private String seatId;
    private String seatNumber;
    private String rowNumber;
    private String seatType;
    private BigDecimal price;
}