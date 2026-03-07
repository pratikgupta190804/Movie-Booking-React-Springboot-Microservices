package com.moviebooking.show.service;

import com.moviebooking.show.dtos.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ShowService {

    ShowResponseDto createShow(CreateShowRequestDto requestDto);

    ShowResponseDto updateShow(String showId, UpdateShowRequestDto requestDto);

    ShowResponseDto getShowById(String showId);

    List<ShowResponseDto> getShowsByMovie(String movieId);

    List<ShowResponseDto> getShowsByTheatre(String theatreId);

    List<ShowResponseDto> getShowsByScreen(String screenId);
    
    List<ShowResponseDto> getShowsByMovieAndTheatre(String movieId, String theatreId, LocalDate date);
    
    List<ShowResponseDto> getShowsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    void cancelShow(String showId);

}