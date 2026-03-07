package com.moviebooking.show.dtos.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private String id;
    private String title;
    private String description;
    private Set<String> languages;
    private Integer durationInMinutes;
    private LocalDate releaseDate;
    private String certificate;
    private String posterUrl;
    private BigDecimal rating;
    private String status;
}
