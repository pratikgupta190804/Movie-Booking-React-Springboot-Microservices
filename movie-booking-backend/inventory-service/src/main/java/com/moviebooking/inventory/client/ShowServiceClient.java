package com.moviebooking.inventory.client;

import com.moviebooking.inventory.dtos.ShowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "show-service", url = "${show-service.url:http://localhost:8084}")
public interface ShowServiceClient {

    @GetMapping("/api/shows/{id}")
    ShowDto getShowById(@PathVariable("id") String id);
}
