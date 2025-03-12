package com.example.firebaselearning.network;

import com.example.firebaselearning.dto.auth.response.UserCommunityShipResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MembershipClient {

    @GET("membership/findAllByUserId")
    Observable<List<UserCommunityShipResponse>> findAllByUserId();

    @GET("membership/findAllByCommunityId/{communityId}")
    Observable<List<UserCommunityShipResponse>> findAllByCommunityId(@Path("communityId") Long communityId);

    @POST("membership/{communityId}")
    Single<UserCommunityShipResponse> create(@Path("communityId") Long communityId);

    @DELETE("membership/deleteByCommunityId/{communityId}")
    Completable deleteByCommunityId(@Path("communityId") Long communityId);

}
