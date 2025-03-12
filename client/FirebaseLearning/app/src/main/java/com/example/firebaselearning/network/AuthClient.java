package com.example.firebaselearning.network;

import com.example.firebaselearning.dto.auth.request.LoginRequestDto;
import com.example.firebaselearning.dto.auth.request.RefreshRequest;
import com.example.firebaselearning.dto.auth.request.RegisterRequestDto;
import com.example.firebaselearning.dto.auth.response.TokensDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthClient {
    @POST("auth/register")
    Call<TokensDto> create(@Body RegisterRequestDto dto);

    @POST("auth/login")
    Call<TokensDto> login(@Body LoginRequestDto dto);

    @POST("auth/refresh")
    Call<TokensDto> refreshToken(@Body RefreshRequest dto);
}
