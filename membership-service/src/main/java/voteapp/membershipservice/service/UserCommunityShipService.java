package voteapp.membershipservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import voteapp.membershipservice.model.UserCommunityShip;
import voteapp.membershipservice.repository.UserCommunityShipReactiveRepository;
import voteapp.membershipservice.util.UserContext;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCommunityShipService {

    private final UserCommunityShipReactiveRepository userCommunityShipReactiveRepository;

    public Flux<UserCommunityShip> findAll() {
        return userCommunityShipReactiveRepository.findAll();
    }

    public Flux<UserCommunityShip> findAllByUserId() {
        return UserContext.getUserId()
                .flatMapMany(userId -> {
                    log.info("Request received for userId: {}", userId);
                    return userCommunityShipReactiveRepository.findAllByUserId(userId)
                            .doOnTerminate(() -> log.info("Request processing completed for userId: {}", userId))
                            .doOnError(e -> log.error("Error occurred while fetching data for userId: {}", userId, e));
                })
                .onErrorResume(e -> {
                    log.error("Error in findAllByUserId", e);
                    return Flux.error(e);
                });
    }

    public Mono<Void> save(UserCommunityShip userCommunityShip) {
        return userCommunityShipReactiveRepository.save(userCommunityShip).then();
    }

    public Mono<UserCommunityShip> save(Long communityId) {
        return UserContext.getUserId()
                .flatMap(userId -> {
                    log.info("Request received for userId: {}", userId);
                    UserCommunityShip userCommunityShip = new UserCommunityShip();
                    userCommunityShip.setCommunityId(communityId);
                    userCommunityShip.setUserId(userId);
                    return userCommunityShipReactiveRepository.save(userCommunityShip);
                });
//        return Mono.fromSupplier(() -> {
//            UserCommunityShip userCommunityShip = new UserCommunityShip();
//            userCommunityShip.setCommunityId(communityId);
//            userCommunityShip.setUserId(UserContext.getUserId());
//            return userCommunityShip;
//        }).flatMap(userCommunityShipReactiveRepository::save);
    }

    public Mono<Void> deleteByCommunityId(Long communityId) {
        return UserContext.getUserId()
                .flatMap(userId -> userCommunityShipReactiveRepository.deleteByUserIdAndCommunityId(userId, communityId));
//        return userCommunityShipReactiveRepository.deleteByUserIdAndCommunityId(UserContext.getUserId(), communityId);
    }
}
