package com.example.proiectmobilebanking.network.api;

import com.example.proiectmobilebanking.network.model.LoginRequest;
import com.example.proiectmobilebanking.network.model.LoginResponse;
import com.example.proiectmobilebanking.network.model.FeedbackRequest;
import com.example.proiectmobilebanking.network.model.FeedbackResponse;
import com.example.proiectmobilebanking.network.model.RegisterRequest;
import com.example.proiectmobilebanking.network.model.TransitionResponse;
import com.example.proiectmobilebanking.network.model.TransferRequest;
import com.example.proiectmobilebanking.network.model.UserInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<Void> register(@Body RegisterRequest request);

    @GET("user/current-user")
    Call<UserInfo> getCurrentUser(@Header("Authorization") String authorization);

    @POST("user/transfer")
    Call<Void> transfer(@Header("Authorization") String authorization, @Body TransferRequest request);

    @GET("transition/current")
    Call<List<TransitionResponse>> getCurrentTransitions(@Header("Authorization") String authorization);

    @GET("transition/received")
    Call<List<TransitionResponse>> getReceivedTransitions(@Header("Authorization") String authorization);

    @GET("feedback/current")
    Call<List<FeedbackResponse>> getCurrentFeedback(@Header("Authorization") String authorization);

    @POST("feedback")
    Call<FeedbackResponse> createFeedback(@Header("Authorization") String authorization, @Body FeedbackRequest request);

    @DELETE("feedback/{id}")
    Call<Void> deleteFeedback(@Header("Authorization") String authorization, @Path("id") Integer id);

}
