package com.example.firebaselearning.model;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Community {
    private Long id;

    private String title;

    private String description;

    private boolean privateFlag;

    private UUID admin;

    private List<UUID> moderators;

}