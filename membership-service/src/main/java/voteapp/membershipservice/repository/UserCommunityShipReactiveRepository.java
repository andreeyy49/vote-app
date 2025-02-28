package voteapp.membershipservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import voteapp.membershipservice.model.UserCommunityShip;

import java.util.UUID;

public interface UserCommunityShipReactiveRepository extends R2dbcRepository<UserCommunityShip, Integer> {

    Flux<UserCommunityShip> findAllByUserId(UUID userId);
    Flux<UserCommunityShip> findAllByCommunityId(Long communityId);
    Mono<Void> deleteByUserIdAndCommunityId(UUID userId, Long communityId);
}
