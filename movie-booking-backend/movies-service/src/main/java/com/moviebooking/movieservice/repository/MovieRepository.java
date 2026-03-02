package com.moviebooking.movieservice.repository;

import com.moviebooking.movieservice.entities.Actor;
import com.moviebooking.movieservice.entities.Genre;
import com.moviebooking.movieservice.entities.Movie;
import com.moviebooking.movieservice.entities.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Movie> searchByTitle(@Param("keyword") String keyword);

    @Query("SELECT m FROM Movie m WHERE m.rating >= :rating ORDER BY m.rating DESC")
    List<Movie> findByRatingGreaterThan(@Param("rating") BigDecimal rating);

    List<Movie> findByStatus(MovieStatus status);

    List<Movie> findByStatusOrderByReleaseDateDesc(MovieStatus status);

    Optional<Movie> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    List<Movie> findMoviesByGenreId(@Param("genreId") String genreId);

    @Query("SELECT m FROM Movie m JOIN m.actors a WHERE a.id = :actorId")
    List<Movie> findMoviesByActorId(@Param("actorId") String actorId);
}