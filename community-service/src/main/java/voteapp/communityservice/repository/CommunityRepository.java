package voteapp.communityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import voteapp.communityservice.model.Community;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query(value = "SELECT c FROM Community c WHERE to_tsvector('russian', c.title) @@ plainto_tsquery(:fragment)", nativeQuery = true)
    List<Community> findByTitleFragment(@Param("fragment") String fragment);

    Community findByTitle(String title);
}
