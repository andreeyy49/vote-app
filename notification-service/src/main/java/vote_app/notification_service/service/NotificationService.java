package vote_app.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vote_app.notification_service.model.UserFCMShip;
import vote_app.notification_service.repository.UserFCMShipRepository;
import vote_app.notification_service.util.UserContext;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final UserFCMShipRepository userFCMShipRepository;

    public void setToken(String token) {
        try {
            log.info("Setting token for user: {}", UserContext.getUserId());
            UserFCMShip userFCMShip = findById();
            if (userFCMShip == null) {
                log.info("UserFCMShip not found, creating a new one");
                userFCMShip = new UserFCMShip();
                userFCMShip.setToken(token);
                userFCMShip.setUserId(UserContext.getUserId());
            } else {
                log.info("UserFCMShip already exists, updating token");
                userFCMShip.setToken(token);
            }
            userFCMShipRepository.save(userFCMShip);
        } catch (Exception e) {
            log.error("Error in setToken: ", e);
        }
    }

    public UserFCMShip findById() {
        return userFCMShipRepository.findById(UserContext.getUserId()).orElse(null);
    }

    public UserFCMShip findById(UUID id) {
        return userFCMShipRepository.findById(id).orElse(null);
    }
}
