package voteapp.communityservice.dto;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoAutoStart
public class CommunityEvent {

    private UUID userId;

    private Long communityId;
}
