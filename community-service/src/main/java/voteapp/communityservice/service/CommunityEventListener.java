package voteapp.communityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityEventListener {
    private final CommunityService communityService;

    @KafkaListener(topics = "${app.kafka.kafkaEventDeleteTopic}", groupId = "${app.kafka.kafkaEventGroupId}")
    public void handleUserCreated(ConsumerRecord<String, String> record) {
        log.info("Received message: {}", record.value());

        Long communityId = Long.parseLong(record.value());
        communityService.deleteById(communityId);

        log.info("Получено событие: сообщество {} удалено", communityId);
    }
}