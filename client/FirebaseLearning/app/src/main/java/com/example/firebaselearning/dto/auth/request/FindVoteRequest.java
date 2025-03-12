package com.example.firebaselearning.dto.auth.request;

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
