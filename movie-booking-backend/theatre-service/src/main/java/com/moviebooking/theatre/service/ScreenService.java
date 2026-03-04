package com.moviebooking.theatre.service;

import com.moviebooking.theatre.dtos.ScreenLayoutRequestDto;
import com.moviebooking.theatre.dtos.ScreenResponseDto;

import java.util.List;

public interface ScreenService {

    ScreenResponseDto createScreenWithLayout(
            String theatreId,
            ScreenLayoutRequestDto requestDto,
            String ownerId
    );

    ScreenResponseDto updateScreenLayout(
            String screenId,
            ScreenLayoutRequestDto requestDto,
            String ownerId
    );

    ScreenResponseDto getScreenWithSeats(String screenId);

    List<ScreenResponseDto> getScreensWithSeatsByTheatreId(String theatreId);
}
