package com.example.firebaselearning.dto.auth.response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityResponse {

    private Long id;

    private String title;

    private String description;

    private boolean privateFlag;

    private UUID admin;

    private List<UUID> moderators;
}
