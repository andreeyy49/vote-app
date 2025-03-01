package voteapp.votingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindVoteRequest {
    private Long communityId;
    private Boolean published;
}
