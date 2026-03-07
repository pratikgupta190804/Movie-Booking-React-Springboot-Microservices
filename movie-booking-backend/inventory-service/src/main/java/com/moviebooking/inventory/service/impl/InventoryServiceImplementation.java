package com.moviebooking.inventory.service.impl;

import com.moviebooking.inventory.client.TheatreServiceClient;
import com.moviebooking.inventory.dtos.*;
import com.moviebooking.inventory.dtos.external.ScreenDto;
import com.moviebooking.inventory.dtos.external.SeatDto;
import com.moviebooking.inventory.enums.SeatStatus;
import com.moviebooking.inventory.entity.ShowSeatInventory;
import com.moviebooking.inventory.exception.BadRequestException;
import com.moviebooking.inventory.exception.ResourceNotFoundException;
import com.moviebooking.inventory.exception.SeatNotAvailableException;
import com.moviebooking.inventory.repo.ShowSeatInventoryRepository;
import com.moviebooking.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImplementation implements InventoryService {

    private final ShowSeatInventoryRepository inventoryRepository;
    private final TheatreServiceClient theatreServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public void createInventoryForShow(ShowCreatedEvent event) {
        log.info("Creating inventory for show: {}", event.getShowId());

        if (inventoryRepository.existsByShowId(event.getShowId())) {
            log.warn("Inventory already exists for show: {}", event.getShowId());
            return;
        }

        try {
            ScreenDto screen = theatreServiceClient.getScreenById(event.getScreenId());

            if (screen.getSeats() == null || screen.getSeats().isEmpty()) {
                log.error("No seats found for screen: {}", event.getScreenId());
                throw new ResourceNotFoundException("No seats found for screen: " + event.getScreenId());
            }

            List<ShowSeatInventory> inventory = new ArrayList<>();

            for (SeatDto seat : screen.getSeats()) {
                if (seat.getActive() != null && seat.getActive()) {
                    ShowSeatInventory inv = new ShowSeatInventory();
                    inv.setShowId(event.getShowId());
                    inv.setSeatId(seat.getId());
                    inv.setScreenId(event.getScreenId());
                    inv.setStatus(SeatStatus.AVAILABLE);
                    inventory.add(inv);
                }
            }

            inventoryRepository.saveAll(inventory);
            log.info("Created inventory for show: {} with {} seats", event.getShowId(), inventory.size());

        } catch (Exception e) {
            log.error("Error creating inventory for show: {}", event.getShowId(), e);
            throw new RuntimeException("Failed to create inventory: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ShowSeatMapDto getSeatMapForShow(String showId) {
        log.info("Fetching seat map for show: {}", showId);

        List<ShowSeatInventory> inventory = inventoryRepository.findByShowIdOrderBySeatId(showId);

        if (inventory.isEmpty()) {
            throw new ResourceNotFoundException("Inventory not found for show: " + showId);
        }

        String screenId = inventory.get(0).getScreenId();
        ScreenDto screen = theatreServiceClient.getScreenById(screenId);

        Map<String, SeatDto> seatDetailsMap = screen.getSeats().stream()
                .collect(Collectors.toMap(SeatDto::getId, seat -> seat));

        List<SeatInventoryDto> seatInventoryList = new ArrayList<>();

        for (ShowSeatInventory inv : inventory) {
            SeatDto seatDetail = seatDetailsMap.get(inv.getSeatId());
            if (seatDetail != null) {
                SeatInventoryDto dto = SeatInventoryDto.builder()
                        .seatId(inv.getSeatId())
                        .rowLabel(seatDetail.getRowLabel())
                        .seatNumber(seatDetail.getSeatNumber())
                        .seatType(seatDetail.getSeatType())
                        .status(inv.getStatus())
                        .price(BigDecimal.ZERO)
                        .displayRow(seatDetail.getDisplayRow())
                        .displayColumn(seatDetail.getDisplayColumn())
                        .active(seatDetail.getActive())
                        .build();
                seatInventoryList.add(dto);
            }
        }

        long availableCount = inventory.stream()
                .filter(inv -> inv.getStatus() == SeatStatus.AVAILABLE)
                .count();

        ShowSeatMapDto seatMapDto = new ShowSeatMapDto();
        seatMapDto.setShowId(showId);
        seatMapDto.setScreenId(screenId);
        seatMapDto.setScreenName(screen.getName());
        seatMapDto.setTotalSeats(inventory.size());
        seatMapDto.setAvailableSeats((int) availableCount);
        seatMapDto.setSeats(seatInventoryList);

        return seatMapDto;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SeatLockResponseDto lockSeats(SeatLockRequestDto request) {
        log.info("Locking seats for user: {} in show: {}", request.getUserId(), request.getShowId());

        String userId = request.getUserId();
        String showId = request.getShowId();
        List<String> seatIds = request.getSeatIds();

        List<ShowSeatInventory> existingLocks = inventoryRepository
                .findByShowIdAndUserIdAndStatus(showId, userId, SeatStatus.LOCKED);

        if (!existingLocks.isEmpty()) {
            log.info("Releasing {} existing locks for user: {}", existingLocks.size(), userId);
            releaseSeats(existingLocks);
        }

        List<ShowSeatInventory> seats = inventoryRepository
                .findByShowIdAndSeatIdInForUpdate(showId, seatIds);

        if (seats.size() != seatIds.size()) {
            throw new BadRequestException("Some seats not found in inventory");
        }

        List<String> unavailableSeats = seats.stream()
                .filter(s -> s.getStatus() != SeatStatus.AVAILABLE)
                .map(ShowSeatInventory::getSeatId)
                .toList();

        if (!unavailableSeats.isEmpty()) {
            throw new SeatNotAvailableException(
                    "Seats already locked or booked: " + String.join(", ", unavailableSeats));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);

        for (ShowSeatInventory seat : seats) {
            seat.setStatus(SeatStatus.LOCKED);
            seat.setUserId(userId);
            seat.setLockedAt(now);
            seat.setLockExpiresAt(expiresAt);
        }

        inventoryRepository.saveAll(seats);

        SeatReservedEvent event = new SeatReservedEvent(showId, seatIds, userId, expiresAt);
        kafkaTemplate.send("seat-reserved-event", event);

        log.info("Successfully locked {} seats for user: {}", seatIds.size(), userId);

        return new SeatLockResponseDto(seatIds, expiresAt);
    }

    @Override
    @Transactional
    public void confirmSeatsForBooking(PaymentSuccessfulEvent event) {
        log.info("Confirming seats for booking: {}", event.getBookingId());

        String userId = event.getUserId();

        List<ShowSeatInventory> lockedSeats = inventoryRepository
                .findByUserIdAndStatus(userId, SeatStatus.LOCKED);

        if (lockedSeats.isEmpty()) {
            log.error("No locked seats found for user: {}", userId);
            return;
        }

        for (ShowSeatInventory seat : lockedSeats) {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setBookingId(event.getBookingId());
            seat.setLockedAt(null);
            seat.setLockExpiresAt(null);
        }

        inventoryRepository.saveAll(lockedSeats);

        log.info("Confirmed {} seats for booking: {}", lockedSeats.size(), event.getBookingId());
    }

    @Override
    @Transactional
    public void releaseSeatsAfterPaymentFailure(PaymentFailedEvent event) {
        log.info("Releasing seats due to payment failure for user: {}", event.getUserId());

        String userId = event.getUserId();
        String showId = event.getShowId();

        List<ShowSeatInventory> lockedSeats = inventoryRepository
                .findByShowIdAndUserIdAndStatus(showId, userId, SeatStatus.LOCKED);

        releaseSeats(lockedSeats);

        log.info("Released {} seats due to payment failure", lockedSeats.size());
    }

    @Override
    @Transactional
    public void releaseExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();

        List<ShowSeatInventory> expiredSeats = inventoryRepository
                .findByStatusAndLockExpiresAtBefore(SeatStatus.LOCKED, now);

        if (expiredSeats.isEmpty()) {
            return;
        }

        log.info("Found {} expired seat locks", expiredSeats.size());

        releaseSeats(expiredSeats);

        Map<String, List<String>> groupedByShow = expiredSeats.stream()
                .collect(Collectors.groupingBy(
                        ShowSeatInventory::getShowId,
                        Collectors.mapping(ShowSeatInventory::getSeatId, Collectors.toList())
                ));

        for (Map.Entry<String, List<String>> entry : groupedByShow.entrySet()) {
            SeatLockExpiredEvent event = new SeatLockExpiredEvent(entry.getKey(), entry.getValue());
            kafkaTemplate.send("seat-lock-expired-event", event);
        }

        log.info("Released {} expired locks", expiredSeats.size());
    }

    private void releaseSeats(List<ShowSeatInventory> seats) {
        for (ShowSeatInventory seat : seats) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setUserId(null);
            seat.setBookingId(null);
            seat.setLockedAt(null);
            seat.setLockExpiresAt(null);
        }
        inventoryRepository.saveAll(seats);
    }
}
