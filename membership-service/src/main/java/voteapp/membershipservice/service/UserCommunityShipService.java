package voteapp.membershipservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import voteapp.membershipservice.model.UserCommunityShip;
import voteapp.membershipservice.repository.UserCommunityShipRepository;
import voteapp.membershipservice.util.UserContext;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCommunityShipService {

    private final UserCommunityShipRepository userCommunityShipRepository;

    public List<UserCommunityShip> findAll() {
        return userCommunityShipRepository.findAll();
    }

    public List<UserCommunityShip> findAllByUserId() {
        return userCommunityShipRepository.findAllByUserId(UserContext.getUserId());
    }

    public List<UserCommunityShip> findAllByCommunityId(Long communityId) {
        return userCommunityShipRepository.findAllByCommunityId(communityId);
    }

    public UserCommunityShip save(UserCommunityShip userCommunityShip) {
        return userCommunityShipRepository.save(userCommunityShip);
    }

    public UserCommunityShip save(Long communityId) {
        UserCommunityShip userCommunityShip = new UserCommunityShip();
        userCommunityShip.setCommunityId(communityId);
        userCommunityShip.setUserId(UserContext.getUserId());
        return userCommunityShipRepository.save(userCommunityShip);
    }
}
