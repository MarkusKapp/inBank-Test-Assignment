package com.example.backend;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class DecisionRequest {

    @NotBlank(message = "Personal code is required.")
    private String personalCode;

    @NotNull(message = "Loan amount is required.")
    @Min(value = LoanConstraints.MIN_LOAN_AMOUNT, message = "Minimum loan amount is 2000.")
    @Max(value = LoanConstraints.MAX_LOAN_AMOUNT, message = "Maximum loan amount is 10000.")
    private Double loanAmount;

    @NotNull(message = "Loan period is required.")
    @Min(value = LoanConstraints.MIN_LOAN_PERIOD, message = "Minimum loan period is 12 months.")
    @Max(value = LoanConstraints.MAX_LOAN_PERIOD, message = "Maximum loan period is 60 months.")
    private Integer loanPeriod;
}

