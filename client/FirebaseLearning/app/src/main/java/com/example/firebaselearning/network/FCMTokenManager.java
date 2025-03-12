package com.example.firebaselearning.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.firebaselearning.dto.auth.request.TokenRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMTokenManager {

    public static void sendTokenToServer(String token, Context context) {
        NotificationClient client = ApiClient.getInstance(context).create(NotificationClient.class);
        Call<Void> call = client.setToken(new TokenRequest(token));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("FCM", "Токен успешно отправлен на сервер.");
                } else {
                    Log.e("FCM", "Ошибка при отправке токена: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FCM", "Ошибка при выполнении запроса: " + t.getMessage());
            }
        });
    }

    public static void updateFCMToken(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.e("FCM", "Не удалось получить токен", task.getException());
                            return;
                        }

                        // Получаем новый токен
                        String token = task.getResult();
                        Log.d("FCM", "Текущий токен: " + token);

                        // Отправляем токен на сервер
                        sendTokenToServer(token, context);
                    }
                });
    }
}