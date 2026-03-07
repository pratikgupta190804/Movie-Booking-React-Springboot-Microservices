package com.moviebooking.inventory.dtos;

import com.moviebooking.inventory.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatInventoryDto {
    private String seatId;
    private String rowLabel;
    private Integer seatNumber;
    private String seatType;
    private SeatStatus status;
    private BigDecimal price;
    private Integer displayRow;
    private Integer displayColumn;
    private Boolean active;
}
