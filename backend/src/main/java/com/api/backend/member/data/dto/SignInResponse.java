package com.api.backend.member.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class SignInResponse {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
