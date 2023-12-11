package com.api.backend.team.data.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeamRole {
  READER("리더"),
  MATE("메이트");

  private final String description;
}
