package com.example.proiectmobilebanking.network.model;

public class TransitionResponse {
    private Integer id;
    private String fromUser;
    private String toUser;
    private Double amount;
    private Double fee;
    private String created;

    public Integer getId() {
        return id;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getFee() {
        return fee;
    }

    public String getCreated() {
        return created;
    }
}
