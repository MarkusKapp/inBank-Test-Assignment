package com.example.backend;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class DecisionRequest {

    @NotBlank(message = "Personal code is required.")
    private String personalCode;

    @NotNull(message = "Loan amount is required.")
    @Min(value = 2000, message = "Minimum loan amount is 2000.")
    @Max(value = 10000, message = "Maximum loan amount is 10000.")
    private Double loanAmount;

    @NotNull(message = "Loan period is required.")
    @Min(value = 12, message = "Minimum loan period is 12 months.")
    @Max(value = 60, message = "Maximum loan period is 60 months.")
    private Integer loanPeriod;
}

