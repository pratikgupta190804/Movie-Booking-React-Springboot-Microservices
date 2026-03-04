package com.moviebooking.theatre.utils;

import com.moviebooking.theatre.dtos.*;
import com.moviebooking.theatre.entity.Screen;
import com.moviebooking.theatre.entity.Theatre;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class TheatreMapper {

    public Theatre toEntity(TheatreRequestDto dto, String ownerId) {

        if (dto == null) {
            return null;
        }

        Theatre theatre = new Theatre();

        theatre.setName(dto.getName());
        theatre.setBrand(dto.getBrand());
        theatre.setDescription(dto.getDescription());
        theatre.setAddressLine1(dto.getAddressLine1());
        theatre.setAddressLine2(dto.getAddressLine2());
        theatre.setCity(dto.getCity());
        theatre.setState(dto.getState());
        theatre.setCountry(dto.getCountry());
        theatre.setPostalCode(dto.getPostalCode());
        theatre.setLatitude(dto.getLatitude());
        theatre.setLongitude(dto.getLongitude());
        theatre.setContactNumber(dto.getContactNumber());
        theatre.setEmail(dto.getEmail());
        theatre.setOpeningTime(dto.getOpeningTime());
        theatre.setClosingTime(dto.getClosingTime());
        theatre.setFoodCourtAvailable(dto.getFoodCourtAvailable());
        theatre.setParkingAvailable(dto.getParkingAvailable());
        theatre.setWheelchairAccessible(dto.getWheelchairAccessible());

        theatre.setOwnerId(ownerId);
        theatre.setActive(true);
        theatre.setRating(null);

        return theatre;
    }

    public void updateEntity(Theatre theatre, TheatreUpdateDto dto) {

        if (dto.getName() != null) theatre.setName(dto.getName());
        if (dto.getBrand() != null) theatre.setBrand(dto.getBrand());
        if (dto.getDescription() != null) theatre.setDescription(dto.getDescription());
        if (dto.getAddressLine1() != null) theatre.setAddressLine1(dto.getAddressLine1());
        if (dto.getAddressLine2() != null) theatre.setAddressLine2(dto.getAddressLine2());
        if (dto.getCity() != null) theatre.setCity(dto.getCity());
        if (dto.getState() != null) theatre.setState(dto.getState());
        if (dto.getCountry() != null) theatre.setCountry(dto.getCountry());
        if (dto.getPostalCode() != null) theatre.setPostalCode(dto.getPostalCode());
        if (dto.getRating() != null) theatre.setRating(dto.getRating());
        if (dto.getLatitude() != null) theatre.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) theatre.setLongitude(dto.getLongitude());
        if (dto.getContactNumber() != null) theatre.setContactNumber(dto.getContactNumber());
        if (dto.getEmail() != null) theatre.setEmail(dto.getEmail());
        if (dto.getOpeningTime() != null) theatre.setOpeningTime(dto.getOpeningTime());
        if (dto.getClosingTime() != null) theatre.setClosingTime(dto.getClosingTime());
        if (dto.getFoodCourtAvailable() != null) theatre.setFoodCourtAvailable(dto.getFoodCourtAvailable());
        if (dto.getParkingAvailable() != null) theatre.setParkingAvailable(dto.getParkingAvailable());
        if (dto.getWheelchairAccessible() != null) theatre.setWheelchairAccessible(dto.getWheelchairAccessible());

        theatre.setUpdatedAt(LocalDateTime.now());
    }

    public TheatreResponseDto toResponse(Theatre theatre) {

        if (theatre == null) {
            return null;
        }

        TheatreResponseDto dto = new TheatreResponseDto();

        dto.setId(theatre.getId());
        dto.setName(theatre.getName());
        dto.setBrand(theatre.getBrand());
        dto.setDescription(theatre.getDescription());
        dto.setAddressLine1(theatre.getAddressLine1());
        dto.setAddressLine2(theatre.getAddressLine2());
        dto.setCity(theatre.getCity());
        dto.setState(theatre.getState());
        dto.setCountry(theatre.getCountry());
        dto.setPostalCode(theatre.getPostalCode());
        dto.setLatitude(theatre.getLatitude());
        dto.setLongitude(theatre.getLongitude());
        dto.setContactNumber(theatre.getContactNumber());
        dto.setEmail(theatre.getEmail());
        dto.setActive(theatre.getActive());
        dto.setRating(theatre.getRating());
        dto.setOpeningTime(theatre.getOpeningTime());
        dto.setClosingTime(theatre.getClosingTime());
        dto.setFoodCourtAvailable(theatre.getFoodCourtAvailable());
        dto.setParkingAvailable(theatre.getParkingAvailable());
        dto.setWheelchairAccessible(theatre.getWheelchairAccessible());
        dto.setCreatedAt(theatre.getCreatedAt());
        dto.setUpdatedAt(theatre.getUpdatedAt());

        if (theatre.getScreens() != null) {
            dto.setScreens(
                    theatre.getScreens()
                            .stream()
                            .map(this::toScreenSummary)
                            .collect(Collectors.toSet())
            );
        }

        return dto;
    }

    private ScreenSummaryDto toScreenSummary(Screen screen) {

        ScreenSummaryDto dto = new ScreenSummaryDto();

        dto.setId(screen.getId());
        dto.setName(screen.getName());
        dto.setScreenType(screen.getScreenType().name());
        dto.setTotalSeats(screen.getTotalSeats());

        return dto;
    }
}