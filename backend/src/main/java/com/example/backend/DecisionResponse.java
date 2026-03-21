package com.example.backend;


import lombok.Data;

@Data
public class DecisionResponse {
    private boolean approved;
    private double approvedAmount;
    private int approvedPeriod;
    private String message;

    public static DecisionResponse approved(double amount, int period, String message) {
        DecisionResponse r = new DecisionResponse();
        r.approved = true;
        r.approvedAmount = amount;
        r.approvedPeriod = period;
        r.message = message;
        return r;
    }

    public static DecisionResponse denied(String message) {
        DecisionResponse r = new DecisionResponse();
        r.approved = false;
        r.approvedAmount = 0.0;
        r.approvedPeriod = 0;
        r.message = message;
        return r;
    }
}

