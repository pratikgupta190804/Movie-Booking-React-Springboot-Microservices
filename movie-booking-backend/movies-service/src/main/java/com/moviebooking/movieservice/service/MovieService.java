package com.moviebooking.movieservice.service;

import com.moviebooking.movieservice.dtos.MovieRequestDto;
import com.moviebooking.movieservice.dtos.MovieResponseDto;
import com.moviebooking.movieservice.dtos.MovieUpdateDto;
import com.moviebooking.movieservice.entities.MovieStatus;

import java.math.BigDecimal;
import java.util.List;

public interface MovieService {

    MovieResponseDto addMovie(MovieRequestDto requestDto, String userId);

    MovieResponseDto getMovieById(String movieId);

    MovieResponseDto getMovieBySlug(String slug);

    List<MovieResponseDto> getMoviesByRatingGreaterThan(BigDecimal rating);

    List<MovieResponseDto> getMoviesByStatus(MovieStatus status);

    List<MovieResponseDto> searchMoviesByTitle(String keyword);

    MovieResponseDto updateMovie(String movieId, MovieUpdateDto updateDto, String userId);

    MovieResponseDto updateStatus(String movieId, MovieStatus status, String userId);

}
