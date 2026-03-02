package com.moviebooking.movieservice.service;

import com.moviebooking.movieservice.dtos.ActorDetailsDto;
import com.moviebooking.movieservice.dtos.ActorDto;

import java.time.LocalDate;
import java.util.List;

public interface ActorService {

    ActorDto createActor(String name, LocalDate dateOfBirth, String bio, String imageUrl, String userId);

    ActorDto getActorById(String actorId);

    ActorDetailsDto getActorDetailsById(String actorId);

    List<ActorDto> getAllActors();

    ActorDto updateActor(String actorId, String name, LocalDate dateOfBirth, String bio, String imageUrl, String userId);

    void deleteActor(String actorId, String userId);
}
