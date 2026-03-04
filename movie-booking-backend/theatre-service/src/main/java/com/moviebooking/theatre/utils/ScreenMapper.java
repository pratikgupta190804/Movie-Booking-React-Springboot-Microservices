package com.moviebooking.theatre.utils;

import com.moviebooking.theatre.dtos.ScreenResponseDto;
import com.moviebooking.theatre.dtos.SeatDto;
import com.moviebooking.theatre.entity.Screen;
import com.moviebooking.theatre.entity.Seat;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScreenMapper {

    public ScreenResponseDto toResponseWithSeats(Screen screen) {
        ScreenResponseDto dto = new ScreenResponseDto();
        dto.setId(screen.getId());
        dto.setName(screen.getName());
        dto.setScreenType(screen.getScreenType() != null ? screen.getScreenType().name() : null);
        dto.setTotalRows(screen.getTotalRows());
        dto.setMaxSeatsPerRow(screen.getMaxSeatsPerRow());
        dto.setTotalSeats(screen.getTotalSeats());
        dto.setTheatreId(screen.getTheatre() != null ? screen.getTheatre().getId() : null);
        dto.setTheatreName(screen.getTheatre() != null ? screen.getTheatre().getName() : null);
        dto.setCreatedAt(screen.getCreatedAt());
        dto.setUpdatedAt(screen.getUpdatedAt());

        if (screen.getSeats() != null && !screen.getSeats().isEmpty()) {
            List<SeatDto> seatDtos = screen.getSeats().stream()
                    .map(this::toSeatDto)
                    .sorted(Comparator
                            .comparing(SeatDto::getDisplayRow)
                            .thenComparing(SeatDto::getDisplayColumn))
                    .collect(Collectors.toList());
            dto.setSeats(seatDtos);
        }

        return dto;
    }

    public ScreenResponseDto toResponse(Screen screen) {
        ScreenResponseDto dto = new ScreenResponseDto();
        dto.setId(screen.getId());
        dto.setName(screen.getName());
        dto.setScreenType(screen.getScreenType() != null ? screen.getScreenType().name() : null);
        dto.setTotalRows(screen.getTotalRows());
        dto.setMaxSeatsPerRow(screen.getMaxSeatsPerRow());
        dto.setTotalSeats(screen.getTotalSeats());
        dto.setTheatreId(screen.getTheatre() != null ? screen.getTheatre().getId() : null);
        dto.setTheatreName(screen.getTheatre() != null ? screen.getTheatre().getName() : null);
        dto.setCreatedAt(screen.getCreatedAt());
        dto.setUpdatedAt(screen.getUpdatedAt());
        return dto;
    }

    private SeatDto toSeatDto(Seat seat) {
        SeatDto dto = new SeatDto();
        dto.setId(seat.getId());
        dto.setRowLabel(seat.getRowLabel());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setSeatType(seat.getSeatType());
        dto.setActive(seat.getActive());
        dto.setDisplayRow(seat.getDisplayRow());
        dto.setDisplayColumn(seat.getDisplayColumn());
        return dto;
    }
}