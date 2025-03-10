package voteapp.membershipservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEventDto {
    private UUID userId;
    private String title;
    private String body;
}
