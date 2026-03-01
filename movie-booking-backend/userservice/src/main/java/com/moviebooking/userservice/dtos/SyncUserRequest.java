package com.moviebooking.userservice.dtos;

import com.moviebooking.userservice.model.Provider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncUserRequest {

    @NotBlank
    private String id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String userName;

    @NotBlank
    private String name;

    @URL
    private String image;

    private Provider provider;

    private String providerId;

    private String role;

}
