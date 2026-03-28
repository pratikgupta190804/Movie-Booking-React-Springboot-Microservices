package com.moviebooking.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String id;
    private String email;
    private String userName;
    private String name;
    private String image;
    private boolean enabled = true;
    private LocalDateTime createdAt ;
    private LocalDateTime updatedAt ;

    private String provider;
    private  String providerId;

    private String role;
}
