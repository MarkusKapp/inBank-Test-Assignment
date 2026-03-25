package com.example.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DecisionResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Objects.requireNonNullElse(e.getDefaultMessage(), "Invalid request."))
                .findFirst()
                .orElse("Invalid request.");

        logger.warn("Validation error: {}", message);
        return ResponseEntity.badRequest().body(DecisionResponse.denied(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DecisionResponse> handleMalformedJson() {
        logger.warn("Malformed or unreadable request body");
        return ResponseEntity.badRequest().body(DecisionResponse.denied("Malformed or missing request body."));
    }
}