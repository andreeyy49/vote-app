package voteapp.usersservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import voteapp.usersservice.model.User;
import voteapp.usersservice.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/findThisAccount")
    public User findThisAccount() {
        return userService.findThisAccount();
    }

}
