package com.api.backend.member.data.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Authority {
  USER("사용자"),
  ADMIN("관리자");

  private final String description;

  }
