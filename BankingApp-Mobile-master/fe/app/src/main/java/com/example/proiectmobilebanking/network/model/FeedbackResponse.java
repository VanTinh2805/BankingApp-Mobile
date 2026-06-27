package com.example.proiectmobilebanking.network.model;

import androidx.annotation.NonNull;

public class FeedbackResponse {
    private Integer id;
    private String userEmail;
    private String suggestion;
    private Integer rating;
    private String created;

    public Integer getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public Integer getRating() {
        return rating;
    }

    public String getCreated() {
        return created;
    }

    @NonNull
    @Override
    public String toString() {
        String ratingText = rating == null ? "0" : String.valueOf(rating);
        String suggestionText = suggestion == null ? "" : suggestion;
        return ratingText + "/5 - " + suggestionText;
    }
}
