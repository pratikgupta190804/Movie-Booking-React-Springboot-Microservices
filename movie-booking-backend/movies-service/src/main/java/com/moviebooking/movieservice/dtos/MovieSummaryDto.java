package com.moviebooking.movieservice.dtos;

import com.moviebooking.movieservice.entities.MovieStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovieSummaryDto {

    private String id;
    private String title;
    private String slug;
    private BigDecimal rating;
    private MovieStatus status;
}