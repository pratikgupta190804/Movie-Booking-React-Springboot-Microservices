package com.moviebooking.movieservice.repository;

import com.moviebooking.movieservice.entities.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, String> {

}
