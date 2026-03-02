package com.moviebooking.movieservice.dtos;

import com.moviebooking.movieservice.entities.Certificate;
import com.moviebooking.movieservice.entities.Language;
import com.moviebooking.movieservice.entities.MovieStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class MovieResponseDto {

    private String id;
    private String title;

    private String description;

    private Set<Language> languages;

    private Integer durationInMinutes;

    private LocalDate releaseDate;

    private Certificate certificate;
    private String posterUrl;
    private String trailerUrl;

    private BigDecimal rating;
    private String country;
    private BigDecimal budget;
    private BigDecimal boxOfficeCollection;

    private MovieStatus status;

    private String slug;

    private Set<GenreDto> genres;

    private Set<ActorDto> actors;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
