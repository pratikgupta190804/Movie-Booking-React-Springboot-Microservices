package com.moviebooking.movieservice.dtos;

import com.moviebooking.movieservice.entities.Certificate;
import com.moviebooking.movieservice.entities.Language;
import com.moviebooking.movieservice.entities.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class MovieRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotEmpty(message = "At least one language is required")
    private Set<Language> languages;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer durationInMinutes;

    private LocalDate releaseDate;

    @NotNull(message = "Certificate is required")
    private Certificate certificate;

    private String posterUrl;

    private String trailerUrl;

    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be between 0 and 10")
    @DecimalMax(value = "10.0", inclusive = true, message = "Rating must be between 0 and 10")
    private BigDecimal rating;

    private String country;

    private BigDecimal budget;

    private BigDecimal boxOfficeCollection;

    @NotNull(message = "Status is required")
    private MovieStatus status;

    @NotBlank(message = "Slug is required")
    private String slug;

    private Set<String> genreIds;

    private Set<String> actorIds;
}
