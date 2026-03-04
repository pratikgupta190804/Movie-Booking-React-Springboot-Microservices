package com.moviebooking.theatre.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Data
public class TheatreResponseDto {

    private String id;

    private String name;
    private String brand;
    private String description;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    private Double latitude;
    private Double longitude;

    private String contactNumber;
    private String email;

    private Boolean active;

    private BigDecimal rating;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private Boolean foodCourtAvailable;
    private Boolean parkingAvailable;
    private Boolean wheelchairAccessible;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // optional
    private Set<ScreenSummaryDto> screens;
}
