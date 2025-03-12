package com.example.firebaselearning.model;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voting {
    private String id;
    private String title;
    private String description;
    private HashMap<String, Integer> choices;
    private HashMap<String, String> castVote;
    private String date;
    private Long communityId;
    private boolean isPublished;
}
