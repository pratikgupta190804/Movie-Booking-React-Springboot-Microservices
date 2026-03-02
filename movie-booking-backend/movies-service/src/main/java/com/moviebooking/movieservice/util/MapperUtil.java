package com.moviebooking.movieservice.util;

import com.moviebooking.movieservice.dtos.*;
import com.moviebooking.movieservice.entities.Actor;
import com.moviebooking.movieservice.entities.Genre;
import com.moviebooking.movieservice.entities.Movie;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MapperUtil {

    public MovieResponseDto toMovieResponse(Movie movie) {
        if (movie == null) {
            return null;
        }

        MovieResponseDto dto = new MovieResponseDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setLanguages(movie.getLanguages());
        dto.setDurationInMinutes(movie.getDurationInMinutes());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setCertificate(movie.getCertificate());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setTrailerUrl(movie.getTrailerUrl());
        dto.setRating(movie.getRating());
        dto.setCountry(movie.getCountry());
        dto.setBudget(movie.getBudget());
        dto.setBoxOfficeCollection(movie.getBoxOfficeCollection());
        dto.setStatus(movie.getStatus());
        dto.setSlug(movie.getSlug());
        dto.setCreatedAt(movie.getCreatedAt());
        dto.setUpdatedAt(movie.getUpdatedAt());

        if (movie.getGenres() != null) {
            dto.setGenres(movie.getGenres().stream()
                    .map(this::toGenreDto)
                    .collect(Collectors.toSet()));
        }

        if (movie.getActors() != null) {
            dto.setActors(movie.getActors().stream()
                    .map(this::toActorDto)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public MovieSummaryDto toMovieSummary(Movie movie) {
        if (movie == null) {
            return null;
        }

        MovieSummaryDto dto = new MovieSummaryDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setSlug(movie.getSlug());
        dto.setRating(movie.getRating());
        dto.setStatus(movie.getStatus());
        return dto;
    }

    public GenreDto toGenreDto(Genre genre) {
        if (genre == null) {
            return null;
        }

        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public GenreDetailsDto toGenreDetails(Genre genre) {
        if (genre == null) {
            return null;
        }

        GenreDetailsDto dto = new GenreDetailsDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());

        if (genre.getMovies() != null) {
            dto.setMovies(genre.getMovies().stream()
                    .map(this::toMovieSummary)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public ActorDto toActorDto(Actor actor) {
        if (actor == null) {
            return null;
        }

        ActorDto dto = new ActorDto();
        dto.setId(actor.getId());
        dto.setName(actor.getName());
        dto.setDateOfBirth(actor.getDateOfBirth());
        dto.setBio(actor.getBio());
        dto.setImageUrl(actor.getImageUrl());
        return dto;
    }

    public ActorDetailsDto toActorDetails(Actor actor) {
        if (actor == null) {
            return null;
        }

        ActorDetailsDto dto = new ActorDetailsDto();
        dto.setId(actor.getId());
        dto.setName(actor.getName());
        dto.setDateOfBirth(actor.getDateOfBirth());
        dto.setBio(actor.getBio());
        dto.setImageUrl(actor.getImageUrl());

        if (actor.getMovies() != null) {
            dto.setMovies(actor.getMovies().stream()
                    .map(this::toMovieSummary)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public void updateMovieFromRequest(MovieRequestDto dto, Movie movie, Set<Genre> genres, Set<Actor> actors) {
        if (dto.getTitle() != null) {
            movie.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            movie.setDescription(dto.getDescription());
        }
        if (dto.getLanguages() != null) {
            movie.setLanguages(dto.getLanguages());
        }
        if (dto.getDurationInMinutes() != null) {
            movie.setDurationInMinutes(dto.getDurationInMinutes());
        }
        if (dto.getReleaseDate() != null) {
            movie.setReleaseDate(dto.getReleaseDate());
        }
        if (dto.getCertificate() != null) {
            movie.setCertificate(dto.getCertificate());
        }
        if (dto.getPosterUrl() != null) {
            movie.setPosterUrl(dto.getPosterUrl());
        }
        if (dto.getTrailerUrl() != null) {
            movie.setTrailerUrl(dto.getTrailerUrl());
        }
        if (dto.getRating() != null) {
            movie.setRating(dto.getRating());
        }
        if (dto.getCountry() != null) {
            movie.setCountry(dto.getCountry());
        }
        if (dto.getBudget() != null) {
            movie.setBudget(dto.getBudget());
        }
        if (dto.getBoxOfficeCollection() != null) {
            movie.setBoxOfficeCollection(dto.getBoxOfficeCollection());
        }
        if (dto.getStatus() != null) {
            movie.setStatus(dto.getStatus());
        }
        if (dto.getSlug() != null) {
            movie.setSlug(dto.getSlug());
        }
        if (genres != null) {
            movie.setGenres(genres);
        }
        if (actors != null) {
            movie.setActors(actors);
        }
    }

    public void updateMovieFromUpdateDto(MovieUpdateDto dto, Movie movie, Set<Genre> genres, Set<Actor> actors) {
        if (dto.getTitle() != null) {
            movie.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            movie.setDescription(dto.getDescription());
        }
        if (dto.getLanguages() != null) {
            movie.setLanguages(dto.getLanguages());
        }
        if (dto.getDurationInMinutes() != null) {
            movie.setDurationInMinutes(dto.getDurationInMinutes());
        }
        if (dto.getReleaseDate() != null) {
            movie.setReleaseDate(dto.getReleaseDate());
        }
        if (dto.getCertificate() != null) {
            movie.setCertificate(dto.getCertificate());
        }
        if (dto.getPosterUrl() != null) {
            movie.setPosterUrl(dto.getPosterUrl());
        }
        if (dto.getTrailerUrl() != null) {
            movie.setTrailerUrl(dto.getTrailerUrl());
        }
        if (dto.getRating() != null) {
            movie.setRating(dto.getRating());
        }
        if (dto.getCountry() != null) {
            movie.setCountry(dto.getCountry());
        }
        if (dto.getBudget() != null) {
            movie.setBudget(dto.getBudget());
        }
        if (dto.getBoxOfficeCollection() != null) {
            movie.setBoxOfficeCollection(dto.getBoxOfficeCollection());
        }
        if (dto.getSlug() != null) {
            movie.setSlug(dto.getSlug());
        }
        if (genres != null) {
            movie.setGenres(genres);
        }
        if (actors != null) {
            movie.setActors(actors);
        }
    }
}
