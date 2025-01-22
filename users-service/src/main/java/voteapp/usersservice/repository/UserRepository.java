package voteapp.usersservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import voteapp.usersservice.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailAndPassword(String email, String password);
}
