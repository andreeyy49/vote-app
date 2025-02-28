package voteapp.membershipservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table(name = "user_community_ship")
@AllArgsConstructor
@NoArgsConstructor
public class UserCommunityShip {

    @Id
    private Integer id;

    @Column("community_id")
    private Long communityId;

    @Column("user_id")
    private UUID userId;
}
