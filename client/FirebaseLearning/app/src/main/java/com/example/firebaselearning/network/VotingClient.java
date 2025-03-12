package com.example.firebaselearning.network;

import com.example.firebaselearning.dto.auth.request.CastVoteRequest;
import com.example.firebaselearning.dto.auth.request.FindVoteRequest;
import com.example.firebaselearning.model.Voting;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VotingClient {

    @POST("voting/findAllByCommunityIdAndPublished")
    Observable<List<Voting>> findAllByCommunityIdAndPublished(@Body FindVoteRequest findVoteRequest);

    @GET("voting/{id}")
    Single<Voting> findById(@Path("id") String id);

    @GET("voting/findAllByUserNotVoted/{communityId}")
    Observable<List<Voting>> findAllByUserNotVoted(@Path("communityId") Long communityId);

    @GET("voting/findAllByUserVoted/{communityId}")
    Observable<List<Voting>> findAllByUserVoted(@Path("communityId") Long communityId);

    @POST("voting")
    Single<Voting> save(@Body Voting voting);

    @POST("voting/castVote")
    Single<Voting> castVote(@Body CastVoteRequest castVoteRequest);

    @PUT("voting/publishVote/{voteId}")
    Completable publishVote(@Path("voteId") String voteId);

    @DELETE("voting/{voteId}")
    Completable deleteVote(@Path("voteId") String voteId);
}
