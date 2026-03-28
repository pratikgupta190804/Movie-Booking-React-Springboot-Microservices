package com.moviebooking.ticket.client;

import com.moviebooking.ticket.dtos.external.MovieDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// client/MovieServiceClient.java
@FeignClient(
        name = "movie-service",
        url = "${movie-service.url}"
)
public interface MovieServiceClient {

    @GetMapping("/api/movies/{movieId}")
    MovieDto getMovieById(@PathVariable("movieId") String movieId);
}
