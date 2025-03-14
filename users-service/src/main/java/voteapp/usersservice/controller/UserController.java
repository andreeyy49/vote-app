package voteapp.usersservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import voteapp.usersservice.model.User;
import voteapp.usersservice.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/findThisAccount")
    public User findThisAccount() {
        return userService.findThisAccount();
    }

    @PostMapping("/findAllByIds")
    public List<User> findAllByIds(@RequestBody List<UUID> ids) {
        return userService.findAllByIds(ids);
    }

    @PutMapping("/update")
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

}