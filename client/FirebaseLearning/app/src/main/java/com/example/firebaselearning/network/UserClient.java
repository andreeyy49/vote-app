package com.example.firebaselearning.network;

import com.example.firebaselearning.model.User;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserClient {

    @POST("user/findAllByIds")
    Call<List<User>> findAllByIds(@Body List<UUID> ids);

    @PUT("user/update")
    Call<User> update(@Body User user);

    @GET("user/findThisAccount")
    Call<User> findThisAccount();

}

