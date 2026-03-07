package com.moviebooking.show.service.impl;

import com.moviebooking.show.client.MovieServiceClient;
import com.moviebooking.show.client.TheatreServiceClient;
import com.moviebooking.show.dtos.CreateShowRequestDto;
import com.moviebooking.show.dtos.SeatPriceRequestDto;
import com.moviebooking.show.dtos.ShowResponseDto;
import com.moviebooking.show.dtos.UpdateShowRequestDto;
import com.moviebooking.show.dtos.external.MovieDto;
import com.moviebooking.show.dtos.external.ScreenDto;
import com.moviebooking.show.dtos.external.TheatreDto;
import com.moviebooking.show.dtos.kafkaDtos.ShowCreatedEvent;
import com.moviebooking.show.entity.Show;
import com.moviebooking.show.entity.ShowSeatPrice;
import com.moviebooking.show.entity.ShowStatus;
import com.moviebooking.show.exception.ConflictException;
import com.moviebooking.show.exception.ResourceNotFoundException;
import com.moviebooking.show.repo.ShowRepository;
import com.moviebooking.show.service.ShowService;
import com.moviebooking.show.utils.ShowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowServiceImplementation implements ShowService {

    private final ShowRepository showRepository;
    private final ShowMapper showMapper;
    private final MovieServiceClient movieServiceClient;
    private final TheatreServiceClient theatreServiceClient;
    private final KafkaTemplate<String, ShowCreatedEvent> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    @Override
    public ShowResponseDto createShow(CreateShowRequestDto requestDto) {

        // Validate movie exists
        try {
            MovieDto movie = movieServiceClient.getMovieById(requestDto.getMovieId());
            log.info("Validated movie: {}", movie.getTitle());
        } catch (Exception e) {
            log.error("Movie validation failed for movieId: {}", requestDto.getMovieId());
            throw new ResourceNotFoundException("Movie not found with ID: " + requestDto.getMovieId());
        }

        // Validate theatre and screen exist
        try {
            TheatreDto theatre = theatreServiceClient.getTheatreById(requestDto.getTheatreId());
            ScreenDto screen = theatreServiceClient.getScreenById(requestDto.getScreenId());
            
            if (!screen.getTheatreId().equals(requestDto.getTheatreId())) {
                throw new ConflictException("Screen does not belong to the specified theatre");
            }
            
            log.info("Validated theatre: {} and screen: {}", theatre.getName(), screen.getName());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Theatre/Screen validation failed", e);
            throw new ResourceNotFoundException("Theatre or Screen not found");
        }

        Show show = new Show();

        show.setMovieId(requestDto.getMovieId());
        show.setTheatreId(requestDto.getTheatreId());
        show.setScreenId(requestDto.getScreenId());
        show.setStartTime(requestDto.getStartTime());
        show.setEndTime(requestDto.getEndTime());
        show.setStatus(ShowStatus.SCHEDULED);

        List<ShowSeatPrice> seatPrices = new ArrayList<>();

        for (SeatPriceRequestDto dto : requestDto.getSeatPrices()) {

            ShowSeatPrice price = new ShowSeatPrice();

            price.setRowLabel(dto.getRowLabel());
            price.setSeatType(dto.getSeatType());
            price.setPrice(dto.getPrice());
            price.setShow(show);

            seatPrices.add(price);
        }

        show.setSeatPrices(seatPrices);

        boolean conflict = showRepository
                .existsByScreenIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        requestDto.getScreenId(),
                        requestDto.getEndTime(),
                        requestDto.getStartTime()
                );

        if (conflict) {
            throw new ConflictException("Screen already has a show during this time");
        }

        Show savedShow = showRepository.save(show);

        ShowCreatedEvent createdEvent = showMapper.toShowCreatedEvent(savedShow);
        try{
            kafkaTemplate.send(topicName, createdEvent.getShowId(), createdEvent);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return showMapper.toResponseDto(savedShow);
    }

    @Override
    public ShowResponseDto updateShow(String showId, UpdateShowRequestDto requestDto) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with Id: "+ showId));

        if(requestDto.getStartTime()!=null)
            show.setStartTime(requestDto.getStartTime());

        if(requestDto.getEndTime()!=null)
            show.setEndTime(requestDto.getEndTime());

        if(requestDto.getStatus()!=null)
            show.setStatus(requestDto.getStatus());

        Show updatedShow = showRepository.save(show);

        return showMapper.toResponseDto(updatedShow);
    }

    @Override
    public ShowResponseDto getShowById(String showId) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        return showMapper.toResponseDto(show);
    }

    @Override
    public List<ShowResponseDto> getShowsByMovie(String movieId) {

        return showRepository.findByMovieIdAndStatus(movieId, ShowStatus.SCHEDULED)
                .stream()
                .map(showMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ShowResponseDto> getShowsByTheatre(String theatreId) {

        return showRepository.findByTheatreIdAndStatus(theatreId, ShowStatus.SCHEDULED)
                .stream()
                .map(showMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ShowResponseDto> getShowsByScreen(String screenId) {

        return showRepository.findByScreenIdAndStatus(screenId, ShowStatus.SCHEDULED)
                .stream()
                .map(showMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ShowResponseDto> getShowsByMovieAndTheatre(String movieId, String theatreId, LocalDate date) {
        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            return showRepository.findByMovieIdAndTheatreIdAndStatusAndStartTimeBetween(
                    movieId, theatreId, ShowStatus.SCHEDULED, startOfDay, endOfDay)
                    .stream()
                    .map(showMapper::toResponseDto)
                    .toList();
        } else {
            return showRepository.findByMovieIdAndTheatreIdAndStatus(movieId, theatreId, ShowStatus.SCHEDULED)
                    .stream()
                    .map(showMapper::toResponseDto)
                    .toList();
        }
    }

    @Override
    public List<ShowResponseDto> getShowsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return showRepository.findShowsInRange(startDate, endDate, ShowStatus.SCHEDULED)
                .stream()
                .map(showMapper::toResponseDto)
                .toList();
    }

    @Override
    public void cancelShow(String showId) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        show.setStatus(ShowStatus.CANCELLED);

        showRepository.save(show);
    }
}