package com.api.backend.category.data.dto;

import com.api.backend.category.type.CategoryType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ScheduleCategoryEditResponse {

  private Long categoryId;
  private String categoryName;
  private CategoryType categoryType;
  private LocalDateTime updateDt;
  private String color;

  public static ScheduleCategoryEditResponse to(ScheduleCategoryDto scheduleCategoryDto) {
    return ScheduleCategoryEditResponse.builder()
        .categoryId(scheduleCategoryDto.getCategoryId())
        .categoryName(scheduleCategoryDto.getCategoryName())
        .categoryType(scheduleCategoryDto.getCategoryType())
        .updateDt(LocalDateTime.now())
        .color(scheduleCategoryDto.getColor())
        .build();
  }
}
