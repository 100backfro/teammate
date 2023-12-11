package com.api.backend.category.data.dto;

import com.api.backend.category.type.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ScheduleCategoryRequest {
  private Long teamId;
  private String categoryName;
  private CategoryType categoryType;
  private String color;
}
