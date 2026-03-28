package com.moviebooking.ticket.exception;

import lombok.*;

import java.time.LocalDateTime;

// exception/ErrorResponseDto.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDto {
    private int status;
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
}
