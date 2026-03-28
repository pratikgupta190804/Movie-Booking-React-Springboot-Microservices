package com.moviebooking.ticket.dtos.external;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDto {

    private String title;

    private String languages;

    private Integer durationInMinutes;

    private String posterUrl;

}
