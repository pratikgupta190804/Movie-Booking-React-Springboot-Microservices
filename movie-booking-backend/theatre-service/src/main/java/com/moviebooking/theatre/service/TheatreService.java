package com.moviebooking.theatre.service;

import com.moviebooking.theatre.dtos.TheatreRequestDto;
import com.moviebooking.theatre.dtos.TheatreResponseDto;
import com.moviebooking.theatre.dtos.TheatreUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TheatreService {

    TheatreResponseDto createTheatre(TheatreRequestDto requestDto, String ownerId);

    TheatreResponseDto updateTheatre(String theatreId,
                                     TheatreUpdateDto updateDto,
                                     String ownerId);

    TheatreResponseDto activateTheatre(String theatreId, String ownerId);

    void deactivateTheatre(String theatreId, String ownerId);

    TheatreResponseDto getTheatreById(String theatreId);

    Page<TheatreResponseDto> getTheatresByCity(String city, Pageable pageable);

    Page<TheatreResponseDto> getTheatresByBrand(String brand, Pageable pageable);

    Page<TheatreResponseDto> searchTheatresByName(String keyword, Pageable pageable);

    Page<TheatreResponseDto> getTheatresNear(
            double latitude,
            double longitude,
            double radiusInKm,
            Pageable pageable
    );
}
