package voteapp.usersservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import voteapp.usersservice.dto.UserDto;
import voteapp.usersservice.model.User;
import voteapp.usersservice.service.UserService;
import voteapp.usersservice.util.UserMapper;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") UUID id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto save(@RequestBody User user) {
        user = userService.save(user);
        System.out.println(MessageFormat.format("name: {0}, email: {1}, password: {2}", user.getName(), user.getEmail(), user.getPassword()));
        return UserMapper.userToDto(user);
    }

    @PutMapping("/{id}")
    public User update(@RequestBody User user, @PathVariable("id") UUID id) {
        return userService.update(user, id);
    }

    @GetMapping("/login/{email}/{password}")
    public UserDto login(@PathVariable("email") String email, @PathVariable("password") String password) {
        User user = userService.login(email, password);
        System.out.println(MessageFormat.format("email: {0}, password: {1}", email, password));
        return UserMapper.userToDto(user);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }
}
