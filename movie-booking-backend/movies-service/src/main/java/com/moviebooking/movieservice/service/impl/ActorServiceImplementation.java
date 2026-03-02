package com.moviebooking.movieservice.service.impl;

import com.moviebooking.movieservice.dtos.ActorDetailsDto;
import com.moviebooking.movieservice.dtos.ActorDto;
import com.moviebooking.movieservice.entities.Actor;
import com.moviebooking.movieservice.entities.Movie;
import com.moviebooking.movieservice.exception.ResourceNotFoundException;
import com.moviebooking.movieservice.repository.ActorRepository;
import com.moviebooking.movieservice.repository.MovieRepository;
import com.moviebooking.movieservice.service.ActorService;
import com.moviebooking.movieservice.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActorServiceImplementation implements ActorService {

    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;
    private final MapperUtil mapperUtil;

    @Override
    @Transactional
    public ActorDto createActor(String name, LocalDate dateOfBirth, String bio, String imageUrl, String userId) {
        log.info("User {} creating actor: {}", userId, name);

        Actor actor = new Actor();
        actor.setName(name);
        actor.setDateOfBirth(dateOfBirth);
        actor.setBio(bio);
        actor.setImageUrl(imageUrl);

        Actor savedActor = actorRepository.save(actor);

        log.info("Successfully created actor with ID: {}", savedActor.getId());
        return mapperUtil.toActorDto(savedActor);
    }

    @Override
    @Transactional(readOnly = true)
    public ActorDto getActorById(String actorId) {
        log.debug("Fetching actor by ID: {}", actorId);

        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with ID: " + actorId));

        return mapperUtil.toActorDto(actor);
    }

    @Override
    @Transactional(readOnly = true)
    public ActorDetailsDto getActorDetailsById(String actorId) {
        log.debug("Fetching actor details by ID: {}", actorId);

        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with ID: " + actorId));

        return mapperUtil.toActorDetails(actor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActorDto> getAllActors() {
        log.debug("Fetching all actors");

        return actorRepository.findAll()
                .stream()
                .map(mapperUtil::toActorDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ActorDto updateActor(String actorId, String name, LocalDate dateOfBirth, String bio, String imageUrl, String userId) {
        log.info("User {} updating actor ID: {}", userId, actorId);

        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with ID: " + actorId));

        if (name != null) {
            actor.setName(name);
        }
        if (dateOfBirth != null) {
            actor.setDateOfBirth(dateOfBirth);
        }
        if (bio != null) {
            actor.setBio(bio);
        }
        if (imageUrl != null) {
            actor.setImageUrl(imageUrl);
        }

        Actor updatedActor = actorRepository.save(actor);

        log.info("Successfully updated actor with ID: {}", actorId);
        return mapperUtil.toActorDto(updatedActor);
    }

    @Override
    @Transactional
    public void deleteActor(String actorId, String userId) {
        log.info("User {} deleting actor ID: {}", userId, actorId);

        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with ID: " + actorId));

        List<Movie> moviesWithActor = movieRepository.findMoviesByActorId(actorId);
        for (Movie movie : moviesWithActor) {
            movie.getActors().remove(actor);
            movieRepository.save(movie);
        }

        actorRepository.delete(actor);

        log.info("Successfully deleted actor with ID: {} and removed from {} movies", actorId, moviesWithActor.size());
    }
}
