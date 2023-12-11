package com.api.backend.category.type;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType{
  DOCUMENTS ("문서"),
  SCHEDULE ("일정");

  private final String description;
}
