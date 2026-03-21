package com.example.backend;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionService decisionService;

    @PostMapping("/decision")
    public ResponseEntity<DecisionResponse> getDecision(@Valid @RequestBody DecisionRequest request) {
        return ResponseEntity.ok(decisionService.calculateDecision(request));
    }
}
