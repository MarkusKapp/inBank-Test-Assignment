package com.example.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DecisionControllerIntegrationTest {

    private static final String DECISION_URL = "/api/public/decision";
    private static final String APPROVED = "$.approved";
    private static final String APPROVED_AMOUNT = "$.approvedAmount";
    private static final String APPROVED_PERIOD = "$.approvedPeriod";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldApproveWithHighCreditModifier() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010998",
                            "loanAmount": 5000,
                            "loanPeriod": 24
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath(APPROVED).value(true))
                .andExpect(jsonPath(APPROVED_AMOUNT).value(10000.0))
                .andExpect(jsonPath(APPROVED_PERIOD).value(24));
    }

    @Test
    void shouldApproveWithMediumCreditModifier() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010987",
                            "loanAmount": 5000,
                            "loanPeriod": 24
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath(APPROVED).value(true))
                .andExpect(jsonPath(APPROVED_AMOUNT).value(7200.0))
                .andExpect(jsonPath(APPROVED_PERIOD).value(24));
    }

    @Test
    void shouldExtendPeriodWhenCreditScoreLow() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010976",
                            "loanAmount": 10000,
                            "loanPeriod": 12
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath(APPROVED).value(true))
                .andExpect(jsonPath(APPROVED_PERIOD).value(60));
    }

    @Test
    void shouldFallBackToMaxPeriodWithReducedAmount() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010976",
                            "loanAmount": 10000,
                            "loanPeriod": 60
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath(APPROVED).value(true))
                .andExpect(jsonPath(APPROVED_AMOUNT).value(6000.0))
                .andExpect(jsonPath(APPROVED_PERIOD).value(60));
    }

    @Test
    void shouldFindSuitablePeriodWhenExtensionIsEnough() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010976",
                            "loanAmount": 2500,
                            "loanPeriod": 12
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath(APPROVED).value(true))
                .andExpect(jsonPath(APPROVED_AMOUNT).value(2500.0))
                .andExpect(jsonPath(APPROVED_PERIOD).value(25));
    }

    @Test
    void shouldApproveExactMaxAmountWhenScoreSufficient() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010998",
                            "loanAmount": 10000,
                            "loanPeriod": 60
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath(APPROVED).value(true))
                .andExpect(jsonPath(APPROVED_AMOUNT).value(10000.0))
                .andExpect(jsonPath(APPROVED_PERIOD).value(60));
    }

    @Test
    void shouldApproveExactMinAmountWhenScoreSufficient() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010998",
                            "loanAmount": 2000,
                            "loanPeriod": 12
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath(APPROVED).value(true))
                .andExpect(jsonPath(APPROVED_AMOUNT).value(10000.0)) // Current logic returns max
                .andExpect(jsonPath(APPROVED_PERIOD).value(12));
    }

    @Test
    void shouldDenyLoanForDebtCase() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "00000000000",
                            "loanAmount": 5000,
                            "loanPeriod": 24
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath(APPROVED).value(false))
                .andExpect(jsonPath("$.message").value("Loan denied due to debt."));
    }

    @Test
    void shouldReturn400WhenAmountBelowMinimum() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010998",
                            "loanAmount": 100,
                            "loanPeriod": 24
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAmountAboveMaximum() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010998",
                            "loanAmount": 99999,
                            "loanPeriod": 24
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPeriodBelowMinimum() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010998",
                            "loanAmount": 5000,
                            "loanPeriod": 6
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPeriodAboveMaximum() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "49002010998",
                            "loanAmount": 5000,
                            "loanPeriod": 999
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPersonalCodeMissing() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "loanAmount": 5000,
                            "loanPeriod": 24
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPersonalCodeIsInvalidFormat() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personalCode": "123",
                            "loanAmount": 5000,
                            "loanPeriod": 24
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Personal code must consist of 11 digits."));
    }

    @Test
    void shouldReturn400WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post(DECISION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}