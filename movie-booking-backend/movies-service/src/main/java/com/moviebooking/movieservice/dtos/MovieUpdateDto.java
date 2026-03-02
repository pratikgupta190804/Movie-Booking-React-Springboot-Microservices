package com.moviebooking.movieservice.dtos;

import com.moviebooking.movieservice.entities.Certificate;
import com.moviebooking.movieservice.entities.Language;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class MovieUpdateDto {

    private String title;

    private String description;

    private Set<Language> languages;

    @Positive(message = "Duration must be positive")
    private Integer durationInMinutes;

    private LocalDate releaseDate;

    private Certificate certificate;

    private String posterUrl;

    private String trailerUrl;

    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be between 0 and 10")
    @DecimalMax(value = "10.0", inclusive = true, message = "Rating must be between 0 and 10")
    private BigDecimal rating;

    private String country;

    private BigDecimal budget;

    private BigDecimal boxOfficeCollection;

    private String slug;

    private Set<String> genreIds;

    private Set<String> actorIds;
}