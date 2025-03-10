package vote_app.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vote_app.notification_service.model.UserFCMShip;

import java.util.UUID;

public interface UserFCMShipRepository extends JpaRepository<UserFCMShip, UUID> {
}
