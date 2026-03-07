package com.moviebooking.show.repo;

import com.moviebooking.show.entity.Show;
import com.moviebooking.show.entity.ShowStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, String> {

    List<Show> findByMovieIdAndStatus(String movieId, ShowStatus status);
    List<Show> findByTheatreIdAndStatus(String theatreId, ShowStatus status);
    List<Show> findByScreenIdAndStatus(String screenId, ShowStatus status);
    List<Show> findByMovieIdAndTheatreIdAndStatus(String movieId, String theatreId, ShowStatus status);
    List<Show> findByMovieIdAndTheatreIdAndStatusAndStartTimeBetween(
            String movieId, String theatreId, ShowStatus status, LocalDateTime startTime, LocalDateTime endTime);
    List<Show> findByStartTimeBetweenAndStatus(LocalDateTime startTime, LocalDateTime endTime, ShowStatus status);
    List<Show> findByScreenIdAndStartTimeLessThanAndEndTimeGreaterThan(
            String screenId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    boolean existsByScreenIdAndStartTimeLessThanAndEndTimeGreaterThan(
            String screenId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    @Query("""
        SELECT s FROM Show s
        WHERE s.startTime <= :endDate
        AND s.endTime >= :startDate
        AND s.status = :status
    """)
    List<Show> findShowsInRange(
            LocalDateTime startDate,
            LocalDateTime endDate,
            ShowStatus status
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE Show s
        SET s.status = 'RUNNING'
        WHERE s.status = 'SCHEDULED'
        AND s.startTime <= :currentTime
    """)
    int updateShowsToRunning(LocalDateTime currentTime);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Show s
        SET s.status = 'COMPLETED'
        WHERE s.status = 'RUNNING'
        AND s.endTime <= :currentTime
    """)
    int updateShowsToCompleted(LocalDateTime currentTime);
}
