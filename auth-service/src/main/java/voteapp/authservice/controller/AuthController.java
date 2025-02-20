package voteapp.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import voteapp.authservice.dto.request.LoginRequest;
import voteapp.authservice.dto.request.RefreshRequest;
import voteapp.authservice.dto.request.RegistrationRequest;
import voteapp.authservice.dto.response.LoginResponse;
import voteapp.authservice.dto.response.SimpleResponse;
import voteapp.authservice.security.SecurityService;
import voteapp.authservice.service.LoginService;
import voteapp.authservice.service.RegistrationService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;

    private final LoginService loginService;

    private final SecurityService securityService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleResponse registerUser(@RequestBody RegistrationRequest request) {;
        registrationService.registerUser(request);
        return new SimpleResponse(LocalDateTime.now(), "Пользователь успешно создан");
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse authUser(@RequestBody LoginRequest loginRequest) {
        return loginService.authenticateUser(loginRequest);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse refreshToken(@RequestBody RefreshRequest request) {
        return loginService.refreshToken(request.getRefreshToken());
    }

    @GetMapping("/validate-token")
    @ResponseStatus(HttpStatus.OK)
    public boolean validateToken(@RequestHeader("Authorization") String headerAuth) {
        return loginService.validateToken(headerAuth);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logoutUser() {
        securityService.logout();
    }

}
