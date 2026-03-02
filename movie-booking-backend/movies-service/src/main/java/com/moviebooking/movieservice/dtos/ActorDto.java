package com.moviebooking.movieservice.dtos;


import lombok.Data;

import java.time.LocalDate;

@Data
public class ActorDto {

    private String id;

    private String name;

    private LocalDate dateOfBirth;

    private String bio;

    private String imageUrl;

}
