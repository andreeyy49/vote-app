package voteapp.authservice.repository;

import org.springframework.data.repository.CrudRepository;
import voteapp.authservice.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String accessToken);

    void  deleteByUserId(Long userId);

}
