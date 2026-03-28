package com.moviebooking.ticket.client;

import com.moviebooking.ticket.dtos.external.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// client/UserServiceClient.java
@FeignClient(
        name = "user-service",
        url = "${user-service.url}"
)
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    UserDto getUserById(@PathVariable("userId") String userId);
}
