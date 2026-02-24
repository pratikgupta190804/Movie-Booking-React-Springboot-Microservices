package com.moviebooking.userservice.dto;

import com.moviebooking.userservice.model.Provider;
import com.moviebooking.userservice.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String image;

    @NotNull(message = "Role is required")
    private Role role;

    @NotNull(message = "Provider is required")
    private Provider provider;
}