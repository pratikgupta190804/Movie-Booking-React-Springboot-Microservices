package com.moviebooking.show.dtos;

import com.moviebooking.show.entity.ShowStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateShowRequestDto {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String language;

    private BigDecimal price;

    private ShowStatus status;

}