package voteapp.authservice.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Entity
@Table(name = "auth_users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    private UUID id;

    @Column(name = "user_name")
    private String username;

    private String email;

    private String password;

    @PrePersist
    protected void generateUuid() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrdered();
        }
    }


//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    @Builder.Default
//    private UserStatus status = UserStatus.REGISTRATION;
}
