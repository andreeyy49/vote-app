package com.example.firebaselearning.network;

import com.example.firebaselearning.dto.auth.request.TokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationClient {

    @POST("notification/setToken")
    Call<Void> setToken(@Body TokenRequest token);
}
