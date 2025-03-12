package com.example.firebaselearning.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokensDto {

    private String accessToken;
    private String refreshToken;
}
