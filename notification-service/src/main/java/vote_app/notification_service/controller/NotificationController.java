package vote_app.notification_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vote_app.notification_service.dto.TokenRequest;
import vote_app.notification_service.service.NotificationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/setToken")
    public void setToken(@RequestBody TokenRequest token) {
        notificationService.setToken(token.getToken());
    }
}
