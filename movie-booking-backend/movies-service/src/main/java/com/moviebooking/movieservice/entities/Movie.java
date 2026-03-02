package com.moviebooking.movieservice.entities;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movies",
        indexes = {
                @Index(name = "idx_movie_title", columnList = "movie_name"),
                @Index(name = "idx_release_date", columnList = "release_date"),
                @Index(name = "idx_rating", columnList = "rating")
        }
)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "movie_name", nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @ElementCollection(targetClass = Language.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "movie_languages", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "language")
    private Set<Language> languages;

    @Column(name = "duration", nullable = false)
    private Integer durationInMinutes;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Certificate certificate;
    private String posterUrl;
    private String trailerUrl;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "10.0", inclusive = true)
    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;
    private String country;
    private BigDecimal budget;
    private BigDecimal boxOfficeCollection;

    @Enumerated(EnumType.STRING)
    private MovieStatus status;

    @Column(unique = true)
    private String slug;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Genre> genres;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> actors;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
