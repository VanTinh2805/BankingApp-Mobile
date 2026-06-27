package com.example.proiectmobilebanking.network.api;

import com.example.proiectmobilebanking.network.model.LoginRequest;
import com.example.proiectmobilebanking.network.model.LoginResponse;
import com.example.proiectmobilebanking.network.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<Void> register(@Body RegisterRequest request);

}