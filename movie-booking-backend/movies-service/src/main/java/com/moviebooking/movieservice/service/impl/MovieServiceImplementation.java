package com.moviebooking.movieservice.service.impl;

import com.moviebooking.movieservice.dtos.MovieRequestDto;
import com.moviebooking.movieservice.dtos.MovieResponseDto;
import com.moviebooking.movieservice.dtos.MovieUpdateDto;
import com.moviebooking.movieservice.entities.Actor;
import com.moviebooking.movieservice.entities.Genre;
import com.moviebooking.movieservice.entities.Movie;
import com.moviebooking.movieservice.entities.MovieStatus;
import com.moviebooking.movieservice.exception.ResourceNotFoundException;
import com.moviebooking.movieservice.repository.ActorRepository;
import com.moviebooking.movieservice.repository.GenreRepository;
import com.moviebooking.movieservice.repository.MovieRepository;
import com.moviebooking.movieservice.service.MovieService;
import com.moviebooking.movieservice.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImplementation implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final MapperUtil mapperUtil;

    @Override
    @Transactional
    public MovieResponseDto addMovie(MovieRequestDto requestDto, String userId) {
        log.info("User {} adding new movie: {}", userId, requestDto.getTitle());

        Movie movie = new Movie();
        
        Set<Genre> genres = null;
        if (requestDto.getGenreIds() != null && !requestDto.getGenreIds().isEmpty()) {
            genres = new HashSet<>(genreRepository.findAllById(requestDto.getGenreIds()));
        }

        Set<Actor> actors = null;
        if (requestDto.getActorIds() != null && !requestDto.getActorIds().isEmpty()) {
            actors = new HashSet<>(actorRepository.findAllById(requestDto.getActorIds()));
        }

        mapperUtil.updateMovieFromRequest(requestDto, movie, genres, actors);

        Movie savedMovie = movieRepository.saveAndFlush(movie);

        log.info("Successfully added movie with ID: {}", savedMovie.getId());
        return mapperUtil.toMovieResponse(savedMovie);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponseDto getMovieById(String movieId) {
        log.debug("Fetching movie by ID: {}", movieId);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));

        return mapperUtil.toMovieResponse(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponseDto getMovieBySlug(String slug) {
        log.debug("Fetching movie by slug: {}", slug);

        Movie movie = movieRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with slug: " + slug));

        return mapperUtil.toMovieResponse(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponseDto> getMoviesByRatingGreaterThan(BigDecimal rating) {
        log.debug("Fetching movies with rating greater than: {}", rating);

        return movieRepository.findByRatingGreaterThan(rating)
                .stream()
                .map(mapperUtil::toMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponseDto> getMoviesByStatus(MovieStatus status) {
        log.debug("Fetching movies with status: {}", status);

        return movieRepository.findByStatus(status)
                .stream()
                .map(mapperUtil::toMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResponseDto> searchMoviesByTitle(String keyword) {
        log.debug("Searching movies by keyword: {}", keyword);

        return movieRepository.searchByTitle(keyword)
                .stream()
                .map(mapperUtil::toMovieResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovieResponseDto updateMovie(String movieId, MovieUpdateDto updateDto, String userId) {
        log.info("User {} updating movie {}", userId, movieId);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));

        Set<Genre> genres = null;
        if (updateDto.getGenreIds() != null && !updateDto.getGenreIds().isEmpty()) {
            genres = new HashSet<>(genreRepository.findAllById(updateDto.getGenreIds()));
        }

        Set<Actor> actors = null;
        if (updateDto.getActorIds() != null && !updateDto.getActorIds().isEmpty()) {
            actors = new HashSet<>(actorRepository.findAllById(updateDto.getActorIds()));
        }

        mapperUtil.updateMovieFromUpdateDto(updateDto, movie, genres, actors);

        Movie updatedMovie = movieRepository.save(movie);

        log.info("Successfully updated movie with ID: {}", movieId);
        return mapperUtil.toMovieResponse(updatedMovie);
    }

    @Override
    @Transactional
    public MovieResponseDto updateStatus(String movieId, MovieStatus status, String userId) {
        log.info("User {} updating status of movie {} to {}", userId, movieId, status);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));

        movie.setStatus(status);
        Movie updatedMovie = movieRepository.save(movie);

        log.info("Successfully updated status for movie ID: {}", movieId);
        return mapperUtil.toMovieResponse(updatedMovie);
    }

}