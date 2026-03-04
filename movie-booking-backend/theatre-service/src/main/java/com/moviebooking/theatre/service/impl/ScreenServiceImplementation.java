package com.moviebooking.theatre.service.impl;

import com.moviebooking.theatre.dtos.ScreenLayoutRequestDto;
import com.moviebooking.theatre.dtos.ScreenResponseDto;
import com.moviebooking.theatre.dtos.SeatLayoutRequestDto;
import com.moviebooking.theatre.entity.*;
import com.moviebooking.theatre.exception.ForbiddenOperationException;
import com.moviebooking.theatre.exception.ResourceNotFoundException;
import com.moviebooking.theatre.repository.ScreenRepository;
import com.moviebooking.theatre.repository.SeatRepository;
import com.moviebooking.theatre.repository.TheatreRepository;
import com.moviebooking.theatre.service.ScreenService;
import com.moviebooking.theatre.utils.ScreenMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreenServiceImplementation implements ScreenService {

    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;
    private final ScreenMapper screenMapper;

    @Override
    @Transactional
    public ScreenResponseDto createScreenWithLayout(
            String theatreId,
            ScreenLayoutRequestDto requestDto,
            String ownerId) {

        log.info("Creating screen with layout for theatre: {}", theatreId);

        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found"));

        if (!theatre.getOwnerId().equals(ownerId)) {
            throw new ForbiddenOperationException("You are not authorized to add screens to this theatre");
        }

        Screen screen = new Screen();
        screen.setName(requestDto.getName());
        screen.setScreenType(ScreenType.valueOf(requestDto.getScreenType()));
        screen.setTotalRows(requestDto.getTotalRows());
        screen.setMaxSeatsPerRow(requestDto.getMaxSeatsPerRow());
        screen.setTheatre(theatre);

        List<Seat> seats = generateSeatsFromLayout(screen, requestDto.getSeatLayout());
        screen.setTotalSeats(seats.size());

        for (Seat seat : seats) {
            seat.setScreen(screen);      // Set owning side
            screen.getSeats().add(seat); // Maintain bidirectional relation
        }

        Screen savedScreen = screenRepository.saveAndFlush(screen);

        log.info("Created screen with {} seats", seats.size());

        return screenMapper.toResponseWithSeats(savedScreen);
    }

    @Override
    @Transactional
    public ScreenResponseDto updateScreenLayout(
            String screenId,
            ScreenLayoutRequestDto requestDto,
            String ownerId) {

        log.info("Updating screen layout for screen: {}", screenId);

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

        if (!screen.getTheatre().getOwnerId().equals(ownerId)) {
            throw new ForbiddenOperationException("Not authorized");
        }

        screen.setName(requestDto.getName());
        screen.setScreenType(ScreenType.valueOf(requestDto.getScreenType()));
        screen.setTotalRows(requestDto.getTotalRows());
        screen.setMaxSeatsPerRow(requestDto.getMaxSeatsPerRow());

        screen.getSeats().clear();

        screenRepository.flush();

        List<Seat> newSeats = generateSeatsFromLayout(screen, requestDto.getSeatLayout());
        screen.setTotalSeats(newSeats.size());

        for (Seat seat : newSeats) {
            seat.setScreen(screen);
            screen.getSeats().add(seat);
        }

        Screen updatedScreen = screenRepository.saveAndFlush(screen);

        log.info("Updated screen with {} seats", newSeats.size());

        return screenMapper.toResponseWithSeats(updatedScreen);
    }

    @Override
    @Transactional(readOnly = true)
    public ScreenResponseDto getScreenWithSeats(String screenId) {

        Screen screen = screenRepository.findByIdWithSeats(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

        return screenMapper.toResponseWithSeats(screen);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreenResponseDto> getScreensWithSeatsByTheatreId(String theatreId) {

        List<Screen> screens = screenRepository.findByTheatreIdWithSeats(theatreId);

        if (screens.isEmpty()) {
            throw new ResourceNotFoundException("No screens found for this theatre");
        }

        return screens.stream()
                .map(screenMapper::toResponseWithSeats)
                .toList();
    }

    private List<Seat> generateSeatsFromLayout(
            Screen screen,
            List<SeatLayoutRequestDto> layoutRequests) {

        List<Seat> seats = new ArrayList<>();

        for (SeatLayoutRequestDto layoutRequest : layoutRequests) {
            for (int seatNum = layoutRequest.getStartSeatNumber();
                 seatNum <= layoutRequest.getEndSeatNumber();
                 seatNum++) {

                if (layoutRequest.getSkipSeats() != null &&
                        layoutRequest.getSkipSeats().contains(seatNum)) {
                    continue;
                }

                Seat seat = new Seat();
                seat.setRowLabel(layoutRequest.getRowLabel());
                seat.setSeatNumber(seatNum);
                seat.setSeatType(layoutRequest.getSeatType());
                seat.setActive(true);
                seat.setDisplayRow(layoutRequest.getDisplayRow());

                int columnOffset = seatNum - layoutRequest.getStartSeatNumber();
                seat.setDisplayColumn(layoutRequest.getDisplayColumn() + columnOffset);

                seats.add(seat);
            }
        }

        return seats;
    }
}