package voteapp.votingservice.repository;

import reactor.core.publisher.Flux;
import voteapp.votingservice.model.Voting;

public interface CustomVotingRepository {
    Flux<Voting> findNotVotedByCommunityAndUser(Long communityId, String userId);
    Flux<Voting> findVotedByCommunityAndUser(Long communityId, String userId);
}
