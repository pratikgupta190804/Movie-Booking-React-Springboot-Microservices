package com.moviebooking.theatre.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moviebooking.theatre.entity.Screen;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, String> {

    @Query("""
    SELECT s
    FROM Screen s
    LEFT JOIN FETCH s.seats
    WHERE s.id = :screenId
""")
    Optional<Screen> findByIdWithSeats(@Param("screenId") String screenId);

    @Query("""
    SELECT DISTINCT s
    FROM Screen s
    LEFT JOIN FETCH s.seats
    WHERE s.theatre.id = :theatreId
""")
    List<Screen> findByTheatreIdWithSeats(@Param("theatreId") String theatreId);

    @EntityGraph(attributePaths = {"seats", "theatre"})
    Optional<Screen> findWithSeatsById(String id);
}