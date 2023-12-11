package com.api.backend.member.data.dto;


import com.api.backend.member.data.entity.Member;
import com.api.backend.member.data.type.Authority;
import com.api.backend.member.data.type.LoginType;
import com.api.backend.member.data.type.SexType;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @Email
    @NotBlank(message = "이메일은 필수 항목 입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 항목 입니다.")
    private String password;
    @NotBlank(message = "비밀번호 확인은 필수 항목 입니다.")
    private String repassword;
    @NotBlank(message = "이름은 필수 항목 입니다.")
    private String name;
    @NotBlank(message = "별명은 필수 항목 입니다.")
    private String nickName;
    @NotBlank(message = "성별은 필수 항목 입니다.")
    private SexType sexType;
}
