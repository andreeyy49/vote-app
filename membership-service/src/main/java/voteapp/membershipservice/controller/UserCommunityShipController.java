package voteapp.membershipservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import voteapp.membershipservice.model.UserCommunityShip;
import voteapp.membershipservice.service.UserCommunityShipService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/membership")
@RequiredArgsConstructor
public class UserCommunityShipController {

    private final UserCommunityShipService userCommunityShipService;

    @GetMapping
    public List<UserCommunityShip> findAll() {
        return userCommunityShipService.findAll();
    }

    @GetMapping("/findAllByUserId/{userId}")
    public List<UserCommunityShip> findAllByUserId(@PathVariable String userId) {
        return userCommunityShipService.findAllByUserId(UUID.fromString(userId));
    }

    @GetMapping("/findAllByCommunityId/{communityId}")
    public List<UserCommunityShip> findAllByCommunityId(@PathVariable Long communityId) {
        return userCommunityShipService.findAllByCommunityId(communityId);
    }

    @PostMapping
    public UserCommunityShip create(@RequestBody UserCommunityShip userCommunityShip) {
        return userCommunityShipService.save(userCommunityShip);
    }
}
