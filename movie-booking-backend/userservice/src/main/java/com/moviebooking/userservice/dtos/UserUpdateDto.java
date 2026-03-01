package com.moviebooking.userservice.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    @Size(min = 2, max = 50)
    private String name;
    private String userName;
    @URL
    private String image;
}
