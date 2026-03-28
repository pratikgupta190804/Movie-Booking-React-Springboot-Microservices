package com.moviebooking.payment.client;

import com.moviebooking.payment.dtos.external.ShowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// client/ShowServiceClient.java
@FeignClient(
        name = "show-service",
        url = "${show-service.url}"
)
public interface ShowServiceClient {

    @GetMapping("/api/shows/{showId}")
    ShowDto getShowById(@PathVariable("showId") String showId);
}
