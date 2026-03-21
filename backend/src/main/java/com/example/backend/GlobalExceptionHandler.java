package com.example.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DecisionResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Objects.requireNonNullElse(e.getDefaultMessage(), "Invalid request."))
                .findFirst()
                .orElse("Invalid request.");
        return ResponseEntity.badRequest().body(DecisionResponse.denied(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DecisionResponse> handleMalformedJson() {
        return ResponseEntity.badRequest().body(DecisionResponse.denied("Malformed or missing request body."));
    }
}