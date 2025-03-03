package voteapp.communityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import voteapp.communityservice.model.Community;

import java.util.List;
import java.util.UUID;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query(value = "SELECT * FROM community WHERE title ILIKE CONCAT('%', :fragment, '%')", nativeQuery = true)
    List<Community> findByTitleFragment(@Param("fragment") String fragment);

    Community findByTitle(String title);

    Boolean existsByAdmin(UUID userid);

    Boolean existsByModeratorsContains(UUID userId);

    List<Community> findAllByModeratorsContains(UUID userId);

    List<Community> findAllByAdmin(UUID userid);
}
