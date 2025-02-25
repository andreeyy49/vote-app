package voteapp.membershipservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import voteapp.membershipservice.model.UserCommunityShip;
import voteapp.membershipservice.service.UserCommunityShipService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membership")
@RequiredArgsConstructor
public class UserCommunityShipController {

    private final UserCommunityShipService userCommunityShipService;

    @GetMapping
    public List<UserCommunityShip> findAll() {
        return userCommunityShipService.findAll();
    }

    @GetMapping("/findAllByUserId")
    public List<UserCommunityShip> findAllByUserId() {
        return userCommunityShipService.findAllByUserId();
    }

    @GetMapping("/findAllByCommunityId/{communityId}")
    public List<UserCommunityShip> findAllByCommunityId(@PathVariable Long communityId) {
        return userCommunityShipService.findAllByCommunityId(communityId);
    }

    @PostMapping("/{communityId}")
    public UserCommunityShip create(@PathVariable Long communityId) {
        return userCommunityShipService.save(communityId);
    }
}
