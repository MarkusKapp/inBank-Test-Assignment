package com.example.backend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DecisionResponse {
    private boolean approved;
    private Double approvedAmount;
    private Integer approvedPeriod;
    private String message;
}

