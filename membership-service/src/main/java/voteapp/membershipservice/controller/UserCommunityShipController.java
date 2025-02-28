package voteapp.membershipservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import voteapp.membershipservice.model.UserCommunityShip;
import voteapp.membershipservice.service.UserCommunityShipService;
import voteapp.membershipservice.util.UserContext;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/membership")
@RequiredArgsConstructor
@Slf4j
public class UserCommunityShipController {

    private final UserCommunityShipService userCommunityShipService;

    @GetMapping
    public Flux<UserCommunityShip> findAll() {
        return userCommunityShipService.findAll();
    }
    @GetMapping("/findAllByUserId")
    public Flux<UserCommunityShip> findAllByUserId() {
        return userCommunityShipService.findAllByUserId()
                .doOnTerminate(() -> log.info("Request processing completed"))
                .doOnError(e -> log.error("Error occurred while fetching data"));
    }

    @PostMapping("/{communityId}")
    public Mono<UserCommunityShip> create(@PathVariable Long communityId) {
        return userCommunityShipService.save(communityId);
    }

    @DeleteMapping("/deleteByCommunityId/{communityId}")
    public Mono<Void> delete(@PathVariable Long communityId) {
        return userCommunityShipService.deleteByCommunityId(communityId);
    }
}
