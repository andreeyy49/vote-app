package voteapp.votingservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import voteapp.votingservice.model.Voting;

public interface VotingRepository extends ReactiveMongoRepository<Voting, String> {
    Flux<Voting> findAllByCommunityIdAndPublished(Long communityId, Boolean published);
}
