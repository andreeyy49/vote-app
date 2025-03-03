package voteapp.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import voteapp.usersservice.model.User;
import voteapp.usersservice.repository.UserRepository;
import voteapp.usersservice.util.UserContext;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findThisAccount() {
        return findById(UserContext.getUserId());
    }

    public User update(User user, UUID id) {
        User existingUser = findById(id);

        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            return save(existingUser);
        }

        return save(user);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    public List<User> findAllByIds(List<UUID> id) {
        return userRepository.findAllById(id);
    }
}

