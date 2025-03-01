package voteapp.votingservice.service;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import voteapp.votingservice.dto.CastVoteRequest;
import voteapp.votingservice.dto.FindVoteRequest;
import voteapp.votingservice.model.Voting;
import voteapp.votingservice.repository.VotingRepository;
import voteapp.votingservice.util.UserContext;

@Service
@RequiredArgsConstructor
public class VotingService {

    private final VotingRepository votingRepository;

    private final ReactiveMongoTemplate mongoTemplate;

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
        return votingRepository.save(voting);
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

    public Mono<UpdateResult> publishVote(String voteId) {
        Query query = Query.query(Criteria.where("_id").is(voteId));
        Update update = new Update().set("isPublished", true);
        return mongoTemplate.updateFirst(query, update, Voting.class);
    }

    public Mono<Void> deleteVote(String voteId) {
        return votingRepository.deleteById(voteId).then();
    }
}
