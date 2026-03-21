package com.example.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DecisionService {

    public DecisionResponse calculateDecision(DecisionRequest request) {
        String personalCode = request.getPersonalCode();
        Double requestedAmount = request.getLoanAmount();
        Integer requestedPeriod = request.getLoanPeriod();

        log.info("Processing loan request for personalCode={}, amount={}, period={}",
                personalCode, requestedAmount, requestedPeriod);

        Integer creditModifier = getCreditModifier(personalCode);

        if (creditModifier == null) {
            // Debt case
            log.warn("Loan denied due to debt for personalCode={}", personalCode);
            return DecisionResponse.denied("Loan denied due to debt.");
        }

        // Calculate creditScore based on the given formula
        double creditScore = ((double) creditModifier / requestedAmount) * requestedPeriod;

        if (creditScore >= 1.0) {
            // Approved, don't go over the maximum
            double maxAmount = Math.min((double) creditModifier * requestedPeriod, LoanConstraints.MAX_LOAN_AMOUNT);
            log.info("Loan approved for personalCode={}, amount={}, period={}", personalCode, maxAmount, requestedPeriod);

            return DecisionResponse.approved(maxAmount, requestedPeriod, "Loan approved.");

        } else {
            // Credit score < 1 extend the period or reduce the amount
            log.debug("Credit score {} below threshold for personalCode={}, attempting period extension",
                    creditScore, personalCode);
            return findSuitablePeriod(creditModifier, requestedPeriod, requestedAmount);
        }
    }

    private Integer getCreditModifier(String personalCode) {
        return switch (personalCode) {
            case "49002010976" -> 100; // Case 1
            case "49002010987" -> 300; // Case 2
            case "49002010998" -> 1000; // Case 3
            default -> null; // Debt
        };
    }

    private DecisionResponse findSuitablePeriod(int creditModifier, int startPeriod, double requestedAmount) {
        // First try to approve the requested amount by extending the period
        for (int period = startPeriod + 1; period <= LoanConstraints.MAX_LOAN_PERIOD; period++) {
            double creditScore = ((double) creditModifier / requestedAmount) * period;
            if (creditScore >= 1.0) {
                log.info("Loan approved with extended period={}, amount={}", period, requestedAmount);
                return DecisionResponse.approved(requestedAmount, period, "Loan approved with extended period.");
            }
        }

        // Requested amount not achievable, fall back to maximum approvable amount at MAX_LOAN_PERIOD
        double maxAmount = Math.min((double) creditModifier * LoanConstraints.MAX_LOAN_PERIOD, LoanConstraints.MAX_LOAN_AMOUNT);
        if (maxAmount >= LoanConstraints.MIN_LOAN_AMOUNT) {
            log.info("Loan approved with modified amount={}, period={}", maxAmount, LoanConstraints.MAX_LOAN_PERIOD);
            return DecisionResponse.approved(maxAmount, LoanConstraints.MAX_LOAN_PERIOD, "Loan approved with modified amount and period.");
        }
        // Unreachable with current CreditModifiers, but here as backup
        log.warn("No suitable loan found for amount={}, creditModifier={}", requestedAmount, creditModifier);
        return DecisionResponse.denied("No suitable loan period found.");
    }
}
