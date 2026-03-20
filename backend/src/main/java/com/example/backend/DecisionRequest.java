package com.example.backend;

import lombok.Data;

@Data
public class DecisionRequest {
    private String personalCode;
    private Double loanAmount;
    private Integer loanPeriod;
}

