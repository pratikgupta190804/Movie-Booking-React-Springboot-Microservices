package com.moviebooking.inventory.dtos.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private String id;
    private String rowLabel;
    private Integer seatNumber;
    private String seatType;
    private Boolean active;
    private Integer displayRow;
    private Integer displayColumn;
}
