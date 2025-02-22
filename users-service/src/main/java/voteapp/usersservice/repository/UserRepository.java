package voteapp.usersservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import voteapp.usersservice.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

}
