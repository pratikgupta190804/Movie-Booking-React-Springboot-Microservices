package com.moviebooking.payment.client;

import com.moviebooking.payment.dtos.external.TheatreDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// client/TheatreServiceClient.java
@FeignClient(
        name = "theatre-service",
        url = "${theatre-service.url}"
)
public interface TheatreServiceClient {

    @GetMapping("/api/theatres/{theatreId}")
    TheatreDto getTheatreById(@PathVariable("theatreId") String theatreId);
}
