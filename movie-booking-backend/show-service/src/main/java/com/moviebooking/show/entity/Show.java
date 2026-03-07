package com.moviebooking.show.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "shows",
        indexes = {
                @Index(name = "idx_movie_id", columnList = "movieId"),
                @Index(name = "idx_theatre_id", columnList = "theatreId"),
                @Index(name = "idx_screen_id", columnList = "screenId"),
                @Index(name = "idx_start_time", columnList = "startTime")
        })
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String movieId;
    private String screenId;
    private String theatreId;
    private String language;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowSeatPrice> seatPrices;
    @Enumerated(EnumType.STRING)
    private ShowStatus status = ShowStatus.SCHEDULED;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
