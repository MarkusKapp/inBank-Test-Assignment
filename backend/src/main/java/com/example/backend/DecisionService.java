package com.example.backend;

import org.springframework.stereotype.Service;

@Service
public class DecisionService {

    private static final int MIN_LOAN_AMOUNT = 2000;
    private static final int MAX_LOAN_AMOUNT = 10000;
    private static final int MIN_LOAN_PERIOD = 12;
    private static final int MAX_LOAN_PERIOD = 60;

    public DecisionResponse calculateDecision(DecisionRequest request) {
        return DecisionResponse.builder()
                .approved(true)
                .approvedAmount(3000.0)
                .approvedPeriod(20)
                .message("Loan approved with no changes")
                .build();
    }

}

