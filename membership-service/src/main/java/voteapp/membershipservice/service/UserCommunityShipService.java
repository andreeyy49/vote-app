package voteapp.membershipservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import voteapp.membershipservice.model.UserCommunityShip;
import voteapp.membershipservice.repository.UserCommunityShipRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommunityShipService {

    private final UserCommunityShipRepository userCommunityShipRepository;

    public List<UserCommunityShip> findAll() {
        return userCommunityShipRepository.findAll();
    }

    public List<UserCommunityShip> findAllByUserId(UUID userId) {
        return userCommunityShipRepository.findAllByUserId(userId);
    }

    public List<UserCommunityShip> findAllByCommunityId(Long communityId) {
        return userCommunityShipRepository.findAllByCommunityId(communityId);
    }

    public UserCommunityShip save(UserCommunityShip userCommunityShip) {
        return userCommunityShipRepository.save(userCommunityShip);
    }
}
