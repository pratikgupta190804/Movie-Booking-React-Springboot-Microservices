package com.moviebooking.show.utils;

import com.moviebooking.show.dtos.CreateShowRequestDto;
import com.moviebooking.show.dtos.SeatPriceResponseDto;
import com.moviebooking.show.dtos.ShowResponseDto;
import com.moviebooking.show.dtos.UpdateShowRequestDto;
import com.moviebooking.show.dtos.kafkaDtos.SeatPriceEvent;
import com.moviebooking.show.dtos.kafkaDtos.ShowCreatedEvent;
import com.moviebooking.show.entity.Show;
import com.moviebooking.show.entity.ShowSeatPrice;
import com.moviebooking.show.entity.ShowStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShowMapper {

    public Show toEntity(CreateShowRequestDto dto) {
        Show show = new Show();

        show.setMovieId(dto.getMovieId());
        show.setScreenId(dto.getScreenId());
        show.setTheatreId(dto.getTheatreId());
        show.setLanguage(dto.getLanguage());
        show.setStartTime(dto.getStartTime());
        show.setEndTime(dto.getEndTime());
        show.setStatus(ShowStatus.SCHEDULED);
        show.setPrice(dto.getPrice());

        return show;
    }

    public void updateEntity(Show show, UpdateShowRequestDto dto) {

        if (dto.getLanguage() != null){
            show.setLanguage(dto.getLanguage());
        }

        if (dto.getStartTime() != null) {
            show.setStartTime(dto.getStartTime());
        }

        if (dto.getEndTime() != null) {
            show.setEndTime(dto.getEndTime());
        }

        if (dto.getStatus() != null) {
            show.setStatus(dto.getStatus());
        }
    }

    public ShowResponseDto toResponseDto(Show show) {

        ShowResponseDto dto = new ShowResponseDto();

        List<SeatPriceResponseDto> priceResponseDtoList = show
                .getSeatPrices()
                .stream()
                .map(this::toPriceResponseDto)
                .toList();

        dto.setId(show.getId());
        dto.setMovieId(show.getMovieId());
        dto.setScreenId(show.getScreenId());
        dto.setTheatreId(show.getTheatreId());
        dto.setLanguage(show.getLanguage());
        dto.setPrice(show.getPrice());
        dto.setSeatPrices(priceResponseDtoList);
        dto.setStartTime(show.getStartTime());
        dto.setEndTime(show.getEndTime());
        dto.setStatus(show.getStatus());
        dto.setCreatedAt(show.getCreatedAt());
        dto.setUpdatedAt(show.getUpdatedAt());

        return dto;
    }

    private SeatPriceResponseDto toPriceResponseDto(ShowSeatPrice seatPrice){
        SeatPriceResponseDto responseDto = new SeatPriceResponseDto();
        responseDto.setSeatType(seatPrice.getSeatType());
        responseDto.setRowLabel(seatPrice.getRowLabel());
        responseDto.setPrice(seatPrice.getPrice());

        return responseDto;
    }

    public ShowCreatedEvent toShowCreatedEvent(Show show){
        ShowCreatedEvent showCreatedEvent = new ShowCreatedEvent();
        showCreatedEvent.setShowId(show.getId());
        showCreatedEvent.setScreenId(show.getScreenId());
        showCreatedEvent.setTheatreId(show.getTheatreId());
        showCreatedEvent.setMovieId(show.getMovieId());
        showCreatedEvent.setStartTime(show.getStartTime());
        showCreatedEvent.setEndTime(show.getEndTime());
        List<SeatPriceEvent> seatPriceEvents = show
                .getSeatPrices()
                .stream()
                .map(this::toSeatPriceEvent)
                .toList();
        showCreatedEvent.setSeatPrices(seatPriceEvents);

        return showCreatedEvent;
    }

    private SeatPriceEvent toSeatPriceEvent(ShowSeatPrice seatPrice){
        SeatPriceEvent seatPriceEvent = new SeatPriceEvent();
        seatPriceEvent.setRowLabel(seatPrice.getRowLabel());
        seatPriceEvent.setSeatType(seatPrice.getRowLabel());
        seatPriceEvent.setPrice(seatPrice.getPrice());

        return seatPriceEvent;
    }
}