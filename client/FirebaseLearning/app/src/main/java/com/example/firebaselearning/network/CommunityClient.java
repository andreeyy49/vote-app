package com.example.firebaselearning.network;

import com.example.firebaselearning.dto.auth.request.ModeratorRequest;
import com.example.firebaselearning.model.Community;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommunityClient {

    @GET("community/{id}")
    Call<Community> findById(@Path("id") Long id);

    @POST("community/findAllByIds")
    Call<List<Community>> findAllByIds(@Body List<Long> ids);

    @POST("community")
    Call<Community> save(@Body Community community);

    @GET("community/findAllByFragment/{fragment}")
    Call<List<Community>> findAllByFragment(@Path("fragment") String fragment);

    @GET("community/findByTitle/{title}")
    Call<Boolean> findByTitle(@Path("title") String title);

    @GET("community/isAdmin")
    Call<Boolean> isAdmin();

    @GET("community/isModerator")
    Call<Boolean> isModerator();

    @GET("community/findAllByModerator")
    Call<List<Community>> findAllByModerator();

    @GET("community/findAllByAdmin")
    Call<List<Community>> findAllByAdmin();

    @POST("community/createModerator")
    Completable createModerator(@Body ModeratorRequest request);

    @POST("community/removeModerator")
    Completable removeModerator(@Body ModeratorRequest request);

}
