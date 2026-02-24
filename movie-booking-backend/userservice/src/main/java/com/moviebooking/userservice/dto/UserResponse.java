package com.moviebooking.userservice.dto;

import com.moviebooking.userservice.model.Provider;
import com.moviebooking.userservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String image;
    private boolean enabled;
    private Role role;
    private Provider provider;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}