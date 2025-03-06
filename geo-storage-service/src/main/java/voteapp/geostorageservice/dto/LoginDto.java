package voteapp.geostorageservice.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class LoginDto {

    private String timestamp;

    private String accessToken;

    private String refreshToken;
}
