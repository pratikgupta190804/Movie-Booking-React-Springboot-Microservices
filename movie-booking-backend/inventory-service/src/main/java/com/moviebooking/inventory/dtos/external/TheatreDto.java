package com.moviebooking.inventory.dtos.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheatreDto {
    private String id;
    private String name;
    private String brand;
    private String city;
    private String state;
    private String country;
    private Boolean active;
}
