package vote_app.notification_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vote_app.notification_service.dto.NotificationEventDto;
import vote_app.notification_service.model.UserFCMShip;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final FcmNotificationService fcmNotificationService;

    @KafkaListener(topics = "${app.kafka.notificationEventTopic}", groupId = "${app.kafka.notificationEventGroupId}")
    public void handleUserCreated(ConsumerRecord<String, String> record) {
        log.info("Received message: {}", record.value());
        try {
            NotificationEventDto event = objectMapper.readValue(record.value(), NotificationEventDto.class);

            UserFCMShip ship = notificationService.findById(event.getUserId());
            fcmNotificationService.sendNotification(ship.getToken(), event.getTitle(), event.getBody());

            log.info("Получено событие: Пользователь {} оповещение: {}", ship.getUserId(), event.getTitle());
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации Kafka-сообщения", e);
        }
    }
}
