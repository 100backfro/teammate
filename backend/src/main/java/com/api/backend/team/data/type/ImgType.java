package com.api.backend.team.data.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImgType {
  TEAM("팀"),
  PARTICIPANT("참가자");

  private final String description;
}
