package voteapp.votingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import voteapp.votingservice.dto.CastVoteRequest;
import voteapp.votingservice.dto.FindVoteRequest;
import voteapp.votingservice.model.Voting;
import voteapp.votingservice.repository.VotingRepository;
import voteapp.votingservice.repository.VotingRepositoryImpl;
import voteapp.votingservice.util.UserContext;

@Service
@RequiredArgsConstructor
public class VotingService {

    private final VotingRepository votingRepository;

    private final VotingRepositoryImpl votingRepositoryImpl;

    private final ReactiveMongoTemplate mongoTemplate;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.membershipEventTopic}")
    private String membershipEventTopic;

    @Value("${app.kafka.communityModerationEventTopic}")
    private String communityModerationEventTopic;

    public Flux<Voting> findAllByCommunityIdAndPublished(FindVoteRequest findVoteRequest) {
        return votingRepository.findAllByCommunityIdAndPublished(
                findVoteRequest.getCommunityId(),
                findVoteRequest.getPublished()
        );
    }

    public Mono<Voting> findById(String id) {
        return votingRepository.findById(id);
    }

    public Mono<Voting> save(Voting voting) {
        return UserContext.getUserId().flatMap(userId -> {
            kafkaTemplate.send(communityModerationEventTopic, String.valueOf(voting.getCommunityId()));
            return votingRepository.save(voting);
        });
    }

    public Mono<Voting> castVote(CastVoteRequest voteRequest) {
        return UserContext.getUserId()
                .flatMap(userId -> findById(voteRequest.getVoteId())
                        .flatMap(vote -> {
                            vote.getCastVote().put(voteRequest.getChoice(), String.valueOf(userId));
                            vote.getChoices().computeIfPresent(voteRequest.getChoice(), (k, v) -> v + 1);
                            return votingRepository.save(vote);
                        }));
    }

    public Mono<Void> publishVote(String voteId) {
        Query query = Query.query(Criteria.where("_id").is(voteId));
        Update update = new Update().set("published", true);

        return mongoTemplate.updateFirst(query, update, Voting.class)
                .then(votingRepository.findById(voteId)) // находим голосование заново, чтобы взять communityId
                .flatMap(voting -> {
                    kafkaTemplate.send(membershipEventTopic, String.valueOf(voting.getCommunityId()));
                    return Mono.empty();
                });
    }

    public Flux<Voting> findAllByCommunityIdAndUserNotVoted(Long communityId) {
        return UserContext.getUserId().flatMapMany(userId ->
                        votingRepositoryImpl.findNotVotedByCommunityAndUser(communityId, String.valueOf(userId))
                );
    }

    public Flux<Voting> findAllByCommunityIdAndUserVoted(Long communityId) {
        return UserContext.getUserId().flatMapMany(userId ->
                votingRepositoryImpl.findVotedByCommunityAndUser(communityId, String.valueOf(userId))
        );
    }

    public Mono<Void> deleteVote(String voteId) {
        return votingRepository.deleteById(voteId).then();
    }
}
