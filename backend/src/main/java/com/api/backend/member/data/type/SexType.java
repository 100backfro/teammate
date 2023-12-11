package com.api.backend.member.data.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SexType {
  MALE("MALE"),
  FEMALE("FEMALE");

  private final String description;

}
