package voteapp.communityservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "community")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private boolean privateFlag;

    private UUID admin;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "community_moderators", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "moderator_id")
    private List<UUID> moderators;
}