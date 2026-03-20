package com.example.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionService decisionService;

    @PostMapping("/decision")
    public DecisionResponse getDecision(@RequestBody DecisionRequest request) {
        return decisionService.calculateDecision(request);
    }
}
