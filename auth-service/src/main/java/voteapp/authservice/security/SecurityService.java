package voteapp.authservice.security;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import voteapp.authservice.dto.request.LoginRequest;
import voteapp.authservice.dto.request.RegistrationRequest;
import voteapp.authservice.dto.response.LoginResponse;
import voteapp.authservice.exception.RefreshTokenException;
import voteapp.authservice.model.RoleTypeAuth;
import voteapp.authservice.model.User;
import voteapp.authservice.model.RefreshToken;
import voteapp.authservice.repository.UserRepository;
import voteapp.authservice.security.jjwt.JwtUtils;
import voteapp.authservice.service.RefreshTokenService;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    private final UserRepository userAuthRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${app.salt}")
    private String salt;

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        if (loginRequest.getEmail().isBlank() || loginRequest.getPassword().isBlank()) {
            throw new IllegalArgumentException("Email and password must not be null");
        }

        try {
            Authentication authentication = authenticateWithPassword(loginRequest.getEmail(), loginRequest.getPassword());

            AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            return LoginResponse.builder()
                    .accessToken(jwtUtils.generateTokenFromUserId(userDetails.getEmail()))
                    .refreshToken(refreshToken.getToken())
                    .build();
        } catch (IllegalArgumentException e) {
            Authentication authentication = authenticateWithPassword(loginRequest.getEmail(), loginRequest.getPassword());

            AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            return LoginResponse.builder()
                    .accessToken(jwtUtils.generateTokenFromUserId(userDetails.getEmail()))
                    .refreshToken(refreshToken.getToken())
                    .build();
        }
    }

    public Authentication authenticateWithPassword(String email, String password) {
        User userAuth = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userAuth.getEmail(), salt + password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    public void register(RegistrationRequest request) {
        var user = User.builder()
                .username(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(salt + request.getPassword1()))
                .build();

        user.setRoles(Collections.singleton(RoleTypeAuth.ROLE_USER));

        userAuthRepository.save(user);
    }

    public LoginResponse refreshToken(String requestRefreshToken) {
        return refreshTokenService.findByRefreshToken(requestRefreshToken)
                .map(refreshTokenService::checkRefreshToken)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    User tokenOwner = userAuthRepository.findById(userId).orElseThrow(() ->
                            new RefreshTokenException("Exception trying to get token for userId: " + userId));

                    return new LoginResponse(LocalDateTime.now(), jwtUtils.generateTokenFromUserId(tokenOwner.getEmail()), refreshTokenService.createRefreshToken(userId).getToken());
                }).orElseThrow(() -> new RefreshTokenException(requestRefreshToken, "Refresh token not found"));
    }

    public void logout() {
        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentPrincipal instanceof AppUserDetails userDetails) {
            Long userId = userDetails.getId();

            refreshTokenService.deleteByUserId(userId);
        }
    }

}

