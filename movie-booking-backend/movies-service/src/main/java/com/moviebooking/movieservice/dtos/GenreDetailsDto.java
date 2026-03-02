package com.moviebooking.movieservice.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class GenreDetailsDto {

    private String id;

    private String name;

    private Set<MovieSummaryDto> movies;
}