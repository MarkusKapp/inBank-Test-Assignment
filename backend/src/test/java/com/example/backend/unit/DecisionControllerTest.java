package com.example.backend.unit;

import com.example.backend.DecisionController;
import com.example.backend.DecisionRequest;
import com.example.backend.DecisionResponse;
import com.example.backend.DecisionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionControllerTest {

    @Mock
    private DecisionService decisionService;

    @InjectMocks
    private DecisionController decisionController;

    @Test
    void shouldReturnDecisionFromService() {
        DecisionRequest request = new DecisionRequest();
        request.setPersonalCode("1234");
        request.setLoanAmount(1000.0);
        request.setLoanPeriod(12);

        DecisionResponse mockResponse = new DecisionResponse();
        mockResponse.setApproved(true);
        mockResponse.setApprovedAmount(1000.0);
        mockResponse.setMessage("Test Approval");

        when(decisionService.calculateDecision(request)).thenReturn(mockResponse);

        ResponseEntity<DecisionResponse> responseEntity = decisionController.getDecision(request);

        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(mockResponse, responseEntity.getBody());
        verify(decisionService).calculateDecision(request);
    }
}

