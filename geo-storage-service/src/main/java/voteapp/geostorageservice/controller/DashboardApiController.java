package voteapp.geostorageservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import voteapp.geostorageservice.aop.Logging;
import voteapp.geostorageservice.client.AuthClient;
import voteapp.geostorageservice.dto.ImageStaticsDto;
import voteapp.geostorageservice.dto.LoginDto;
import voteapp.geostorageservice.dto.UserLoginDto;
import voteapp.geostorageservice.service.S3StorageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Logging
@Slf4j
public class DashboardApiController {

    private final S3StorageService s3StorageService;
    private final AuthClient authClient;

    @GetMapping("/statics")
    public List<ImageStaticsDto> getStatics() {
        return s3StorageService.getStorageContent();
    }

    @PostMapping("/removeNoUsageImage")
    public List<ImageStaticsDto> removeNoUsageImage() {
        return s3StorageService.removeNoUsageImage();
    }

    @PostMapping("/login")
    public LoginDto generateLink(@RequestParam String email, @RequestParam String password) {
        log.info("LoginDto: email:{}, password:{}", email, password);

        UserLoginDto userLoginDto = new UserLoginDto(email, password);
        LoginDto loginDto = authClient.login(userLoginDto);
        log.info("Token is: {}", loginDto.getAccessToken());

        return loginDto;
    }

}
