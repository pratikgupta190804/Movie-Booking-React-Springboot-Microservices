package com.moviebooking.movieservice.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ActorDetailsDto {

    private String id;

    private String name;

    private LocalDate dateOfBirth;

    private String bio;

    private String imageUrl;

    // Lightweight movie info
    private Set<MovieSummaryDto> movies;
}