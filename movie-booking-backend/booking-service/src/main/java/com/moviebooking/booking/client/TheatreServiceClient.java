package com.moviebooking.booking.client;

import com.moviebooking.booking.dtos.external.ScreenDto;
import com.moviebooking.booking.dtos.external.TheatreDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "theatre-service", url = "${theatre-service.url:http://localhost:8083}")
public interface TheatreServiceClient {

    @GetMapping("/api/theatres/{theatreId}")
    TheatreDto getTheatreById(@PathVariable("theatreId") String theatreId);

    @GetMapping("/api/screens/{screenId}")
    ScreenDto getScreenById(@PathVariable("screenId") String screenId);
}
