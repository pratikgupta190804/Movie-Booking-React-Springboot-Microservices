package com.moviebooking.booking.client;

import com.moviebooking.booking.dtos.external.MovieDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "movie-service", url = "${movie-service.url:http://localhost:8082}")
public interface MovieServiceClient {

    @GetMapping("/api/movies/{movieId}")
    MovieDto getMovieById(@PathVariable("movieId") String movieId);
}
