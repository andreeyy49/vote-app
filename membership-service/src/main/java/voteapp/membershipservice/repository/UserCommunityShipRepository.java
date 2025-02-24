package voteapp.membershipservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import voteapp.membershipservice.model.UserCommunityShip;

import java.util.List;
import java.util.UUID;

public interface UserCommunityShipRepository extends JpaRepository<UserCommunityShip, Long> {

    List<UserCommunityShip> findAllByUserId(UUID userId);
    List<UserCommunityShip> findAllByCommunityId(Long communityId);
}