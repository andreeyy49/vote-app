package voteapp.votingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "votes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Voting {

    @Id
    private String id;
    private String title;
    private String description;
    private Map<String, Integer> choices;  // Варианты голосования
    private Map<String, String> castVote; // Кто как проголосовал (userId -> выбор)
    private Long communityId;
    private boolean published;

}
