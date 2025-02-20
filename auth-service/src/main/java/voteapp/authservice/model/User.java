package voteapp.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Table(name = "auth_users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String username;

    private String email;

    private String password;

//    private String token;

    @ElementCollection(targetClass = RoleTypeAuth.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "roles", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<RoleTypeAuth> roles = new HashSet<>();

//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    @Builder.Default
//    private UserStatus status = UserStatus.REGISTRATION;
}
