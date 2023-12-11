package com.api.backend.member.data.type;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginType {
  KAKAO("카카오"),
  NAVER("네이버"),
  GOOGLE("구글"),
  TEAMMATE("팀메이트");

  private final String description;

}
