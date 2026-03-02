package com.moviebooking.movieservice.service;

import com.moviebooking.movieservice.dtos.GenreDetailsDto;
import com.moviebooking.movieservice.dtos.GenreDto;
import com.moviebooking.movieservice.entities.Genre;

import java.util.List;

public interface GenreService {

    GenreDto createGenre(String name, String userId);

    GenreDto getGenreById(String genreId);

    GenreDetailsDto getGenreDetailsById(String genreId);

    List<GenreDto> getAllGenres();

    GenreDto updateGenre(String genreId, String name, String userId);

    void deleteGenre(String genreId, String userId);
}
