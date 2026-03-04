package com.moviebooking.theatre.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Data
@Entity
@Table(name = "theatres",
        indexes = {
                @Index(name = "idx_lat_lng", columnList = "latitude, longitude"),
                @Index(name = "idx_city", columnList = "city"),
                @Index(name = "idx_active", columnList = "active")
        })
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String brand; //

    private String description;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    private Double latitude;
    private Double longitude;

    private String contactNumber;
    private String email;

    private Boolean active;

    private BigDecimal rating;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private Boolean foodCourtAvailable;
    private Boolean parkingAvailable;
    private Boolean wheelchairAccessible;

    private String ownerId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL)
    private Set<Screen> screens;
}
