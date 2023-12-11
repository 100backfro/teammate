package com.api.backend.member.data.dto;

import com.api.backend.member.data.entity.Member;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {
    private String email;
    private String message;
}
