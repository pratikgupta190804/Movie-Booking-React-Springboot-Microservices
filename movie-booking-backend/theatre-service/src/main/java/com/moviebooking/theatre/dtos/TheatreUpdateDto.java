package com.moviebooking.theatre.dtos;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class TheatreUpdateDto {

    private String name;

    private String brand;

    private String description;

    private String addressLine1;
    private String addressLine2;

    private String city;
    private String state;
    private String country;
    private String postalCode;

    private BigDecimal rating;

    private Double latitude;
    private Double longitude;

    private String contactNumber;

    @Email
    private String email;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private Boolean foodCourtAvailable;
    private Boolean parkingAvailable;
    private Boolean wheelchairAccessible;

}