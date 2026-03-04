package com.moviebooking.theatre.repository;

import com.moviebooking.theatre.entity.Theatre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, String> {

    Page<Theatre> findByBrandIgnoreCaseAndActive(String brand, Boolean active, Pageable pageable);
    Page<Theatre> findByCityIgnoreCaseAndActive(String city, Boolean active, Pageable pageable);

    @Query("SELECT t FROM Theatre t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Theatre> searchByName(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
    SELECT t FROM Theatre t
    WHERE (
        6371 * acos(
            cos(radians(:lat)) *
            cos(radians(t.latitude)) *
            cos(radians(t.longitude) - radians(:lng)) +
            sin(radians(:lat)) *
            sin(radians(t.latitude))
        )
    ) < :radius
""")
    Page<Theatre> findTheatresNear(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radius") double radiusInKm,
            Pageable pageable
    );
}
