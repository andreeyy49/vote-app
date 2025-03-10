package voteapp.membershipservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import voteapp.membershipservice.dto.CommunityEvent;
import voteapp.membershipservice.dto.NotificationEventDto;
import voteapp.membershipservice.model.UserCommunityShip;
import voteapp.membershipservice.repository.UserCommunityShipReactiveRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityEventListener {


    private final ObjectMapper objectMapper;

    private final UserCommunityShipReactiveRepository repository;

    private final UserCommunityShipService service;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.notificationEventTopic}")
    private String notificationTopic;

    @KafkaListener(topics = "${app.kafka.kafkaEventTopic}", groupId = "${app.kafka.kafkaEventGroupId}")
    public void handleUserCreated(ConsumerRecord<String, String> record) {
        log.info("Received message: {}", record.value());
        try {
            CommunityEvent event = objectMapper.readValue(record.value(), CommunityEvent.class);

            UserCommunityShip userCommunityShip = new UserCommunityShip();
            userCommunityShip.setCommunityId(event.getCommunityId());
            userCommunityShip.setUserId(event.getUserId());

            repository.save(userCommunityShip)
                    .doOnSuccess(saved -> System.out.println("Запись сохранена: " + saved))
                    .doOnError(error -> System.err.println("Ошибка сохранения: " + error.getMessage()))
                    .subscribe();

            log.info("Получено событие: Пользователь {} вступил в {}", event.getUserId(), event.getCommunityId());

        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации Kafka-сообщения", e);
        }
    }

    @KafkaListener(topics = "${app.kafka.membershipEventTopic}", groupId = "${app.kafka.notificationEventGroupId}")
    public void handleNotification(ConsumerRecord<String, String> record) {
        log.info("Received message: {}", record.value());
        Long communityId = Long.parseLong(record.value());

        service.findAllByCommunityId(communityId)
                .map(UserCommunityShip::getUserId)
                .doOnNext(userId -> {

                    NotificationEventDto notificationEvent = new NotificationEventDto();
                    notificationEvent.setUserId(userId);
                    notificationEvent.setTitle("Проголосуйте!");
                    notificationEvent.setBody("Добавлено новое голосование");

                    String mappedEvent;

                    try {
                        mappedEvent = objectMapper.writeValueAsString(notificationEvent);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    kafkaTemplate.send(notificationTopic, mappedEvent);
                })
                .subscribe();
    }
}
