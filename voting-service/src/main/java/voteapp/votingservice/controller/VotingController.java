package voteapp.votingservice.controller;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import voteapp.votingservice.dto.CastVoteRequest;
import voteapp.votingservice.dto.FindVoteRequest;
import voteapp.votingservice.model.Voting;
import voteapp.votingservice.service.VotingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/voting")
public class VotingController {

    private final VotingService service;

    @PostMapping("/findAllByCommunityIdAndPublished")
    public Flux<Voting> findAllByCommunityIdAndPublished(@RequestBody FindVoteRequest findVoteRequest) {
        return service.findAllByCommunityIdAndPublished(findVoteRequest);
    }

    @GetMapping("/{id}")
    public Mono<Voting> findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    public Mono<Voting> save(@RequestBody Voting voting) {
        return service.save(voting);
    }

    @PostMapping("/castVote")
    public Mono<Voting> castVote(@RequestBody CastVoteRequest castVoteRequest) {
        return service.castVote(castVoteRequest);
    }

    @PutMapping("/publishVote/{voteId}")
    public Mono<UpdateResult> publishVote(@PathVariable String voteId) {
        return service.publishVote(voteId);
    }

    @DeleteMapping("/{voteId}")
    public Mono<Void> deleteVote(@PathVariable String voteId) {
        return service.deleteVote(voteId);
    }
}
