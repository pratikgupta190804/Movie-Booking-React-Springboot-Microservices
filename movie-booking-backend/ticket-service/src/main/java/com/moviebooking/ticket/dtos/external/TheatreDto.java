package com.moviebooking.ticket.dtos.external;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TheatreDto {
    private String id;
    private String name;
    private String address;
    private String city;
}