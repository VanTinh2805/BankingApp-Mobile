package com.example.proiectmobilebanking.network.model;

public class FeedbackRequest {
    private String suggestion;
    private Integer rating;

    public FeedbackRequest(String suggestion, Integer rating) {
        this.suggestion = suggestion;
        this.rating = rating;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public Integer getRating() {
        return rating;
    }
}
