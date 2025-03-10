package vote_app.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vote_app.notification_service.model.UserFCMShip;
import vote_app.notification_service.repository.UserFCMShipRepository;
import vote_app.notification_service.util.UserContext;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserFCMShipRepository userFCMShipRepository;

    public void setToken(String token) {
        UserFCMShip userFCMShip = findById();
        if(userFCMShip == null) {
            userFCMShip = new UserFCMShip();
            userFCMShip.setToken(token);
            userFCMShip.setUserId(UserContext.getUserId());
        } else {
            userFCMShip.setToken(token);
        }
        userFCMShipRepository.save(userFCMShip);
    }

    public UserFCMShip findById() {
        return userFCMShipRepository.findById(UserContext.getUserId()).orElse(null);
    }

    public UserFCMShip findById(UUID id) {
        return userFCMShipRepository.findById(id).orElse(null);
    }
}
