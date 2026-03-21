package com.moviebooking.payment.exception;

import lombok.*;

import java.time.LocalDateTime;

// ErrorResponseDto.java
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