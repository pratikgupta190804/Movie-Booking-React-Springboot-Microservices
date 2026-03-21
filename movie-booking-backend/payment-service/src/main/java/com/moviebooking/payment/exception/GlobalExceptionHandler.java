package com.moviebooking.payment.exception;


import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── Payment specific exceptions ────────────────────────────────────────
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePaymentNotFoundException(
            PaymentNotFoundException ex) {
        log.error("Payment not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "PAYMENT_NOT_FOUND");
    }

    @ExceptionHandler(PaymentVerificationException.class)
    public ResponseEntity<ErrorResponseDto> handlePaymentVerificationException(
            PaymentVerificationException ex) {
        log.error("Payment verification failed: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "PAYMENT_VERIFICATION_FAILED");
    }

    @ExceptionHandler(InvalidPaymentStateException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidPaymentStateException(
            InvalidPaymentStateException ex) {
        log.error("Invalid payment state: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "INVALID_PAYMENT_STATE");
    }

    @ExceptionHandler(PaymentAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handlePaymentAlreadyExistsException(
            PaymentAlreadyExistsException ex) {
        log.error("Payment already exists: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), "PAYMENT_ALREADY_EXISTS");
    }

    @ExceptionHandler(BookingValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleBookingValidationException(
            BookingValidationException ex) {
        log.error("Booking validation failed: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "BOOKING_VALIDATION_FAILED");
    }

    // ── Validation exceptions ──────────────────────────────────────────────
    // Triggered when @Valid fails on request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("Validation failed: {}", message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, "VALIDATION_FAILED");
    }

    // ── Feign client exceptions ────────────────────────────────────────────
    // When booking-service is down or returns error
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponseDto> handleFeignException(FeignException ex) {
        log.error("Feign client error: {}", ex.getMessage());

        if (ex.status() == 404) {
            return buildErrorResponse(HttpStatus.NOT_FOUND,
                    "Booking service could not find the resource", "BOOKING_SERVICE_NOT_FOUND");
        }
        if (ex.status() == 503) {
            return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE,
                    "Booking service is unavailable", "BOOKING_SERVICE_UNAVAILABLE");
        }

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error communicating with booking service", "BOOKING_SERVICE_ERROR");
    }

    // ── Fallback ───────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", "INTERNAL_SERVER_ERROR");
    }

    // ── Builder ────────────────────────────────────────────────────────────
    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status,
                                                                String message,
                                                                String errorCode) {
        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(status.value())
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(error);
    }
}
