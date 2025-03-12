package com.example.firebaselearning.dto.auth.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCommunityShipResponse {

    private Long communityId;
    private UUID userId;
}
