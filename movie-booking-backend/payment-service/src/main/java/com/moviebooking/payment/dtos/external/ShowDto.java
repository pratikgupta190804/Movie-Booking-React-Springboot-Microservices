package com.moviebooking.payment.dtos.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

// dtos/external/ShowDto.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowDto {
    private String id;
    private String movieId;
    private String theatreId;
    private String screenId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String language;
}
