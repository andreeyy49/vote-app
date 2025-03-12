package com.example.firebaselearning.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.firebaselearning.auth.TokenManager;
import com.example.firebaselearning.dto.auth.request.RefreshRequest;
import com.example.firebaselearning.dto.auth.response.TokensDto;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;
    private final Retrofit retrofit;

    public AuthInterceptor(Context context) {
        this.tokenManager = new TokenManager(context);

        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.71:8080/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String accessToken = tokenManager.getAccessToken();
        Request newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        Log.d("AuthInterceptor", "Access Token: " + accessToken);
        Log.d("AuthInterceptor", "Request URL: " + newRequest.url());
        Log.d("AuthInterceptor", "Request Method: " + newRequest.method());
        Log.d("AuthInterceptor", "Request Headers: " + newRequest.headers());

        Response response = chain.proceed(newRequest);

        Log.d("AuthInterceptor", "HTTP Status Code: " + response.code());
        Log.d("AuthInterceptor", "Response Headers: " + response.headers());

        ResponseBody responseBody = response.body();
        String responseBodyString = responseBody != null ? responseBody.string() : "No response body";
        Log.d("AuthInterceptor", "Response Body: " + responseBodyString);

        response = response.newBuilder().body(ResponseBody.create(responseBody.contentType(), responseBodyString)).build();

        if (response.code() == 401) {
            synchronized (this) {
                Log.d("AuthInterceptor", "Token expired, attempting refresh...");
                String refreshToken = tokenManager.getRefreshToken();
                AuthClient authClient = retrofit.create(AuthClient.class);
                Call<TokensDto> call = authClient.refreshToken(new RefreshRequest(refreshToken));
                retrofit2.Response<TokensDto> tokenResponse = call.execute();

                if (tokenResponse.isSuccessful() && tokenResponse.body() != null) {
                    TokensDto newTokens = tokenResponse.body();
                    tokenManager.saveTokens(newTokens.getAccessToken(), newTokens.getRefreshToken());
                    Log.d("AuthInterceptor", "Token refreshed successfully");

                    Request retriedRequest = originalRequest.newBuilder()
                            .addHeader("Authorization", "Bearer " + newTokens.getAccessToken())
                            .build();
                    return chain.proceed(retriedRequest);
                } else {
                    Log.e("AuthInterceptor", "Token refresh failed, response: " + tokenResponse.errorBody().string());
                }
            }
        }
        return response;
    }
}
