package voteapp.votingservice.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import voteapp.votingservice.model.Voting;

@Slf4j
public class VotingRepositoryImpl implements CustomVotingRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public VotingRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Voting> findVotedByCommunityAndUser(Long communityId, String userId) {
        log.info("Starting findVotedByCommunityAndUser: communityId={}, userId={}", communityId, userId);

        return mongoTemplate.find(
                        Query.query(Criteria
                                .where("communityId").is(communityId)
                                .and("published").is(true)
                        ),
                        Voting.class
                )
                .doOnNext(voting -> log.info("Raw voting found: {}", voting))
                .filter(voting -> {
                    boolean contains = voting.getCastVote() != null
                            && voting.getCastVote().containsValue(userId);
                    log.info("Voting ID {}: contains user vote? {}", voting.getId(), contains);
                    return contains;
                })
                .doOnComplete(() -> log.info("Voted search completed"))
                .doOnError(e -> log.error("Error in findVotedByCommunityAndUser", e));
    }

    @Override
    public Flux<Voting> findNotVotedByCommunityAndUser(Long communityId, String userId) {
        log.info("Starting findNotVotedByCommunityAndUser: communityId={}, userId={}", communityId, userId);

        return mongoTemplate.find(
                        Query.query(Criteria
                                .where("communityId").is(communityId)
                                .and("published").is(true)
                        ),
                        Voting.class
                )
                .doOnNext(voting -> log.info("Raw voting found: {}", voting))
                .filter(voting -> {
                    boolean notContains = voting.getCastVote() == null
                            || !voting.getCastVote().containsValue(userId);
                    log.info("Voting ID {}: user NOT voted? {}", voting.getId(), notContains);
                    return notContains;
                })
                .doOnComplete(() -> log.info("Not-voted search completed"))
                .doOnError(e -> log.error("Error in findNotVotedByCommunityAndUser", e));
    }
}