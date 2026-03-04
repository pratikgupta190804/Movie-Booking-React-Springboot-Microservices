package com.moviebooking.theatre.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.moviebooking.theatre.dtos.TheatreRequestDto;
import com.moviebooking.theatre.dtos.TheatreResponseDto;
import com.moviebooking.theatre.dtos.TheatreUpdateDto;
import com.moviebooking.theatre.entity.Theatre;
import com.moviebooking.theatre.exception.ForbiddenOperationException;
import com.moviebooking.theatre.exception.ResourceNotFoundException;
import com.moviebooking.theatre.repository.TheatreRepository;
import com.moviebooking.theatre.service.TheatreService;
import com.moviebooking.theatre.utils.TheatreMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TheatreServiceImplementation implements TheatreService {

    private final TheatreRepository theatreRepository;
    private final TheatreMapper theatreMapper;

    @Override
    public TheatreResponseDto createTheatre(TheatreRequestDto requestDto, String ownerId) {

        Theatre theatre = theatreMapper.toEntity(requestDto, ownerId);

        Theatre saved = theatreRepository.save(theatre);

        return theatreMapper.toResponse(saved);
    }

    @Override
    public TheatreResponseDto updateTheatre(String theatreId,
                                            TheatreUpdateDto updateDto,
                                            String ownerId) {

        Theatre theatre = getOwnedTheatre(theatreId, ownerId);

        theatreMapper.updateEntity(theatre, updateDto);

        Theatre updated = theatreRepository.save(theatre);

        return theatreMapper.toResponse(updated);
    }

    @Override
    public TheatreResponseDto activateTheatre(String theatreId, String ownerId) {

        Theatre theatre = getOwnedTheatre(theatreId, ownerId);

        theatre.setActive(true);
        Theatre updated = theatreRepository.save(theatre);

        return theatreMapper.toResponse(updated);
    }

    @Override
    public void deactivateTheatre(String theatreId, String ownerId) {

        Theatre theatre = getOwnedTheatre(theatreId, ownerId);

        theatre.setActive(false);
        theatre.setUpdatedAt(LocalDateTime.now());

        theatreRepository.save(theatre);
    }

    @Override
    public TheatreResponseDto getTheatreById(String theatreId) {

        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with Id: "+ theatreId));

        return theatreMapper.toResponse(theatre);
    }

    @Override
    public Page<TheatreResponseDto> getTheatresByCity(String city, Pageable pageable) {

        return theatreRepository
                .findByCityIgnoreCaseAndActive(city, true, pageable)
                .map(theatreMapper::toResponse);
    }

    @Override
    public Page<TheatreResponseDto> getTheatresByBrand(String brand, Pageable pageable) {

        return theatreRepository
                .findByBrandIgnoreCaseAndActive(brand, true, pageable)
                .map(theatreMapper::toResponse);
    }

    @Override
    public Page<TheatreResponseDto> searchTheatresByName(String keyword, Pageable pageable) {

        return theatreRepository
                .searchByName(keyword, pageable)
                .map(theatreMapper::toResponse);
    }

    @Override
    public Page<TheatreResponseDto> getTheatresNear(double latitude,
                                                    double longitude,
                                                    double radiusInKm,
                                                    Pageable pageable) {

        return theatreRepository
                .findTheatresNear(latitude, longitude, radiusInKm, pageable)
                .map(theatreMapper::toResponse);
    }

    private Theatre getOwnedTheatre(String theatreId, String ownerId) {

        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found"));

        if (!theatre.getOwnerId().equals(ownerId)) {
            throw new ForbiddenOperationException("You are not authorized to modify this theatre");
        }

        return theatre;
    }
}