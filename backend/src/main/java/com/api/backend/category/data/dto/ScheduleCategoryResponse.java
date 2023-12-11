package com.api.backend.category.data.dto;

import com.api.backend.category.type.CategoryType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
@Builder
public class ScheduleCategoryResponse {

  private Long categoryId;
  private String categoryName;
  private CategoryType categoryType;
  private LocalDateTime createDt;
  private LocalDateTime updateDt;
  private String color;

  public static ScheduleCategoryResponse to(ScheduleCategoryDto scheduleCategoryDto) {
    return ScheduleCategoryResponse.builder()
        .categoryId(scheduleCategoryDto.getCategoryId())
        .categoryName(scheduleCategoryDto.getCategoryName())
        .categoryType(scheduleCategoryDto.getCategoryType())
        .createDt(scheduleCategoryDto.getCreateDt())
        .updateDt(scheduleCategoryDto.getUpdateDt())
        .color(scheduleCategoryDto.getColor())
        .build();
  }

  public static Page<ScheduleCategoryResponse> to(
      Page<ScheduleCategoryDto> scheduleCategoryDtoList) {
    return scheduleCategoryDtoList.map(ScheduleCategoryResponse::to);
  }
}
