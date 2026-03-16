package com.moviebooking.booking.client;

import com.moviebooking.booking.dtos.external.ShowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "show-service", url = "${show-service.url:http://localhost:8084}")
public interface ShowServiceClient {

    @GetMapping("/api/shows/{showId}")
    ShowDto getShowById(@PathVariable("showId") String showId);
}
