package voteapp.communityservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import voteapp.communityservice.dto.CommunityEvent;
import voteapp.communityservice.model.Community;
import voteapp.communityservice.repository.CommunityRepository;
import voteapp.communityservice.util.UserContext;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    @Value("${app.kafka.kafkaEventTopic}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final CommunityRepository communityRepository;

    private final ObjectMapper objectMapper;

    public List<Community> findAll() {
        return communityRepository.findAll();
    }

    public List<Community> findAllById(List<Long> ids) {
        return communityRepository.findAllById(ids);
    }

    public Community save(Community community) {
        CommunityEvent event = new CommunityEvent(UserContext.getUserId(), community.getId());
        String mappedEvent;

        try {
            mappedEvent = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(mappedEvent, topic);
        return communityRepository.save(community);
    }

    public Community findById(Long id) {
        return communityRepository.findById(id).orElse(null);
    }

    public Community update(Community community) {
        Community existing = findById(community.getId());
        existing.setAdmin(community.getAdmin());
        existing.setDescription(community.getDescription());
        existing.setModerators(community.getModerators());
        existing.setPrivateFlag(community.isPrivateFlag());
        existing.setTitle(community.getTitle());

        return save(existing);
    }

    public void deleteById(Long id) {
        communityRepository.deleteById(id);
    }
}
