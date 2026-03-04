package com.moviebooking.theatre.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moviebooking.theatre.entity.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, String> {
    List<Seat> findByScreenIdOrderByDisplayRowAscDisplayColumnAsc(String screenId);
    
    void deleteByScreenId(String screenId);
}