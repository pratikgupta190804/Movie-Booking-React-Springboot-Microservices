package com.moviebooking.movieservice.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenreRequestDto {

    @NotBlank(message = "Genre name is required")
    private String name;
}
