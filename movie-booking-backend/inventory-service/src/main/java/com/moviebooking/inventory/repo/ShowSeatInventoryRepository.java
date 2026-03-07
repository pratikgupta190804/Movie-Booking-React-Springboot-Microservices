package com.moviebooking.inventory.repo;

import com.moviebooking.inventory.enums.SeatStatus;
import com.moviebooking.inventory.entity.ShowSeatInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowSeatInventoryRepository extends JpaRepository<ShowSeatInventory, String> {

    List<ShowSeatInventory> findByShowIdOrderBySeatId(String showId);

    List<ShowSeatInventory> findByShowIdAndStatus(String showId, SeatStatus status);

    List<ShowSeatInventory> findByShowIdAndUserIdAndStatus(String showId, String userId, SeatStatus status);
    
    List<ShowSeatInventory> findByUserIdAndStatus(String userId, SeatStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ShowSeatInventory s WHERE s.showId = :showId AND s.seatId IN :seatIds")
    List<ShowSeatInventory> findByShowIdAndSeatIdInForUpdate(
            @Param("showId") String showId,
            @Param("seatIds") List<String> seatIds
    );

    @Query("SELECT s FROM ShowSeatInventory s WHERE s.status = :status AND s.lockExpiresAt < :expiryTime")
    List<ShowSeatInventory> findByStatusAndLockExpiresAtBefore(
            @Param("status") SeatStatus status,
            @Param("expiryTime") LocalDateTime expiryTime
    );

    long countByShowIdAndStatus(String showId, SeatStatus status);

    boolean existsByShowId(String showId);
}

