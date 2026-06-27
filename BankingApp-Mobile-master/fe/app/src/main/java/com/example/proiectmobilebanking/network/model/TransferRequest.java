package com.example.proiectmobilebanking.network.model;

public class TransferRequest {
    private String receiver;
    private Double amount;

    public TransferRequest(String receiver, Double amount) {
        this.receiver = receiver;
        this.amount = amount;
    }

    public String getReceiver() {
        return receiver;
    }

    public Double getAmount() {
        return amount;
    }
}
