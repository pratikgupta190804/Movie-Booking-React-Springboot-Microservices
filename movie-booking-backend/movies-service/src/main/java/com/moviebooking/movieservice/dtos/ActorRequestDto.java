package com.moviebooking.movieservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ActorRequestDto {

    @NotBlank(message = "Actor name is required")
    private String name;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private String bio;

    private String imageUrl;
}
