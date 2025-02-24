package voteapp.communityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import voteapp.communityservice.model.Community;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

}
