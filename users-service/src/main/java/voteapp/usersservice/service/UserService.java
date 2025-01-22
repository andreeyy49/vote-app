package voteapp.usersservice.service;

import org.springframework.stereotype.Service;
import voteapp.usersservice.model.Role;
import voteapp.usersservice.model.RoleType;
import voteapp.usersservice.model.User;
import voteapp.usersservice.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        Role role = new Role();
        role.setAuthorities(RoleType.USER);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        role.setUser(user);
        user.setRole(roles);

        return userRepository.save(user);
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public User update(User user, UUID id) {
        User existingUser = findById(id);

        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            existingUser.setRole(user.getRole());
            return save(existingUser);
        }

        return save(user);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password).orElse(null);
    }
}
