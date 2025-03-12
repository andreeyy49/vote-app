package vote_app.notification_service.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmNotificationService {

    public String sendNotification(String fcmToken, String title, String body) {
        // Формируем уведомление
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // Формируем сообщение с указанием токена
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .build();

        try {
            // Отправляем сообщение через FCM
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM response: {}", response);
            return response; // ID сообщения
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
