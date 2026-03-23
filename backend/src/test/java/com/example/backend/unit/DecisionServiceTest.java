package com.example.backend.unit;

import com.example.backend.DecisionRequest;
import com.example.backend.DecisionResponse;
import com.example.backend.DecisionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DecisionServiceTest {

    private DecisionService decisionService;

    @BeforeEach
    void setUp() {
        decisionService = new DecisionService();
    }

    @Test
    void shouldDenyWhenPersonalCodeIsDebt() {
        DecisionRequest request = new DecisionRequest();
        request.setPersonalCode("00000000000"); // Debt case
        request.setLoanAmount(5000.0);
        request.setLoanPeriod(12);

        DecisionResponse response = decisionService.calculateDecision(request);

        assertFalse(response.isApproved());
        assertEquals("Loan denied due to debt.", response.getMessage());
        assertEquals(0.0, response.getApprovedAmount());
        assertEquals(0, response.getApprovedPeriod());
    }

    @Test
    void shouldApproveWhenScoreIsSufficient() {
        // Modifier 1000. (1000 / 5000) * 12 = 2.4 >= 1.0
        DecisionRequest request = new DecisionRequest();
        request.setPersonalCode("49002010998");
        request.setLoanAmount(5000.0);
        request.setLoanPeriod(12);

        DecisionResponse response = decisionService.calculateDecision(request);

        assertTrue(response.isApproved());
        // Max amount = min(1000 * 12, 10000) = 10000
        assertEquals(10000.0, response.getApprovedAmount());
        assertEquals(12, response.getApprovedPeriod());
    }

    @Test
    void shouldFindSuitablePeriodWhenScoreLowButExtendable() {
        // Modifier 100. (100 / 2000) * 12 = 0.6 < 1.0
        // Needs score >= 1 => (100/2000) * P >= 1 => 0.05 * P >= 1 => P >= 20
        DecisionRequest request = new DecisionRequest();
        request.setPersonalCode("49002010976");
        request.setLoanAmount(2000.0);
        request.setLoanPeriod(12);

        DecisionResponse response = decisionService.calculateDecision(request);

        assertTrue(response.isApproved());
        assertEquals("Loan approved with extended period.", response.getMessage());
        assertEquals(2000.0, response.getApprovedAmount());
        assertEquals(20, response.getApprovedPeriod());
    }

    @Test
    void shouldFallbackToMaxPeriodWithReducedAmountWhenExtensionFails() {
        // Modifier 100. Requested 7000.
        // Max score possible: (100 / 7000) * 60 = 0.85 < 1.0
        // Fallback: Max amount at 60 months = min(100 * 60, 10000) = 6000.
        DecisionRequest request = new DecisionRequest();
        request.setPersonalCode("49002010976");
        request.setLoanAmount(7000.0);
        request.setLoanPeriod(12);

        DecisionResponse response = decisionService.calculateDecision(request);

        assertTrue(response.isApproved());
        assertEquals("Loan approved with modified amount and period.", response.getMessage());
        assertEquals(6000.0, response.getApprovedAmount());
        assertEquals(60, response.getApprovedPeriod());
    }

    @Test
    void shouldApproveWithMaxPeriodWhenScoreIsExactlyOne() {
         // Modifier 100. Amount 2000. Period 20.
         // Score = (100/2000) * 20 = 1.0.
         // Should approve immediately without searching.
        DecisionRequest request = new DecisionRequest();
        request.setPersonalCode("49002010976");
        request.setLoanAmount(2000.0);
        request.setLoanPeriod(20);

        DecisionResponse response = decisionService.calculateDecision(request);

        assertTrue(response.isApproved());
        // Approved amount = min(100 * 20, 10000) = 2000
        assertEquals(2000.0, response.getApprovedAmount());
        assertEquals(20, response.getApprovedPeriod());
    }
}

