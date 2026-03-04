package com.moviebooking.theatre.dtos;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalTime;

@Data
public class TheatreRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String brand;

    private String description;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @NotBlank
    private String postalCode;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotBlank
    private String contactNumber;

    @Email
    private String email;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private Boolean foodCourtAvailable;
    private Boolean parkingAvailable;
    private Boolean wheelchairAccessible;

}
