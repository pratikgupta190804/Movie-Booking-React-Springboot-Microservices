package com.moviebooking.movieservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "actors",
        indexes = {
                @Index(name = "idx_actor_name", columnList = "name")
        }
)
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private LocalDate dateOfBirth;

    @Column(length = 2000)
    private String bio;

    private String imageUrl;

    @ManyToMany(mappedBy = "actors", fetch = FetchType.LAZY)
    private Set<Movie> movies;
}