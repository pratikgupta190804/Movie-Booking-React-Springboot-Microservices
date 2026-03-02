package com.moviebooking.movieservice.service.impl;

import com.moviebooking.movieservice.dtos.GenreDetailsDto;
import com.moviebooking.movieservice.dtos.GenreDto;
import com.moviebooking.movieservice.entities.Genre;
import com.moviebooking.movieservice.entities.Movie;
import com.moviebooking.movieservice.exception.ResourceNotFoundException;
import com.moviebooking.movieservice.repository.GenreRepository;
import com.moviebooking.movieservice.repository.MovieRepository;
import com.moviebooking.movieservice.service.GenreService;
import com.moviebooking.movieservice.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImplementation implements GenreService {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final MapperUtil mapperUtil;

    @Override
    @Transactional
    public GenreDto createGenre(String name, String userId) {
        log.info("User {} creating genre: {}", userId, name);

        Genre genre = new Genre();
        genre.setName(name);

        Genre savedGenre = genreRepository.save(genre);

        log.info("Successfully created genre with ID: {}", savedGenre.getId());
        return mapperUtil.toGenreDto(savedGenre);
    }

    @Override
    @Transactional(readOnly = true)
    public GenreDto getGenreById(String genreId) {
        log.debug("Fetching genre by ID: {}", genreId);

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with ID: " + genreId));

        return mapperUtil.toGenreDto(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public GenreDetailsDto getGenreDetailsById(String genreId) {
        log.debug("Fetching genre details by ID: {}", genreId);

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with ID: " + genreId));

        return mapperUtil.toGenreDetails(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> getAllGenres() {
        log.debug("Fetching all genres");

        return genreRepository.findAll()
                .stream()
                .map(mapperUtil::toGenreDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GenreDto updateGenre(String genreId, String name, String userId) {
        log.info("User {} updating genre ID: {}", userId, genreId);

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with ID: " + genreId));

        genre.setName(name);
        Genre updatedGenre = genreRepository.save(genre);

        log.info("Successfully updated genre with ID: {}", genreId);
        return mapperUtil.toGenreDto(updatedGenre);
    }

    @Override
    @Transactional
    public void deleteGenre(String genreId, String userId) {
        log.info("User {} deleting genre ID: {}", userId, genreId);

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with ID: " + genreId));

        List<Movie> moviesWithGenre = movieRepository.findMoviesByGenreId(genreId);
        for (Movie movie : moviesWithGenre) {
            movie.getGenres().remove(genre);
            movieRepository.save(movie);
        }

        genreRepository.delete(genre);

        log.info("Successfully deleted genre with ID: {} and removed from {} movies", genreId, moviesWithGenre.size());
    }
}
