package com.example.firebaselearning.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.firebaselearning.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("FCM", "onMessageReceived triggered!");
        Log.d("FCM", "From: " + remoteMessage.getFrom());

        // Проверяем, есть ли уведомление и показываем его
        if (remoteMessage.getNotification() != null) {
            Log.d("FCM", "Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d("FCM", "Notification Body: " + remoteMessage.getNotification().getBody());

            // Показываем уведомление вручную в случае, если приложение в foreground
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        // Дополнительные данные
        if (remoteMessage.getData().size() > 0) {
            Log.d("FCM", "Data Payload: " + remoteMessage.getData());
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "default_channel";

        // Настроить NotificationChannel для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Создаем уведомление
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.notification) // Замените на свою иконку
                .setAutoCancel(true)
                .build();

        // Показываем уведомление
        notificationManager.notify(0, notification); // Идентификатор уведомления
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Новый токен: " + token);

        // Отправляем новый токен на сервер
        FCMTokenManager.sendTokenToServer(token, this);
    }
}
