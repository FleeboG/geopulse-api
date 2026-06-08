package com.geopulse.geopulse_api.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e) {

        HttpStatus status =
                "Zone not found".equals(e.getMessage())
                        ? HttpStatus.NOT_FOUND
                        : HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(status)
                .body(new ApiError(
                        status.name(),
                        e.getMessage(),
                        OffsetDateTime.now()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation failed");

        return ResponseEntity
                .badRequest()
                .body(new ApiError(
                        "VALIDATION_ERROR",
                        message,
                        OffsetDateTime.now()
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException e) {

        return ResponseEntity
                .badRequest()
                .body(new ApiError(
                        "VALIDATION_ERROR",
                        e.getMessage(),
                        OffsetDateTime.now()
                ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
     public ResponseEntity<ApiError> handleHandlerMethodValidation(HandlerMethodValidationException e) {
        return ResponseEntity
                .badRequest()
                .body(new ApiError(
                        "VALIDATION_ERROR",
                        "Validation failed",
                        OffsetDateTime.now()
                ));
        }
}