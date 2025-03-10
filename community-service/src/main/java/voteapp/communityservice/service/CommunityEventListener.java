package voteapp.communityservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import voteapp.communityservice.dto.NotificationEventDto;
import voteapp.communityservice.model.Community;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityEventListener {
    private final CommunityService communityService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.notificationEventTopic}")
    private String kafkaEventTopic;

    @KafkaListener(topics = "${app.kafka.kafkaEventDeleteTopic}", groupId = "${app.kafka.kafkaEventGroupId}")
    public void handleUserCreated(ConsumerRecord<String, String> record) {
        log.info("Received message: {}", record.value());

        Long communityId = Long.parseLong(record.value());
        communityService.deleteById(communityId);

        log.info("Получено событие: сообщество {} удалено", communityId);
    }

    @KafkaListener(topics = "${app.kafka.communityModerationEventTopic}", groupId = "${app.kafka.notificationEventGroupId}")
    public void handleNotification(ConsumerRecord<String, String> record) {
        log.info("Received message: {}", record.value());

        Long communityId = Long.parseLong(record.value());
        Community community = communityService.findById(communityId);

        String mappedEvent;

        NotificationEventDto eventDto = new NotificationEventDto();

        for(UUID userId : community.getModerators()) {

            eventDto.setUserId(userId);
            eventDto.setTitle("Уведомление модерации!");
            eventDto.setBody("Поступило голосование на модерацию!");

            try {
                mappedEvent = objectMapper.writeValueAsString(eventDto);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            kafkaTemplate.send(kafkaEventTopic, mappedEvent);
        }

        eventDto.setUserId(community.getAdmin());
        eventDto.setTitle("Уведомление модерации!");
        eventDto.setBody("Поступило голосование на модерацию!");

        try {
            mappedEvent = objectMapper.writeValueAsString(eventDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(kafkaEventTopic, mappedEvent);
    }
}