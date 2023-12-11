package com.api.backend.comment.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEditRequest {
  @NotBlank
  @Schema(description = "editor id", example = "12L")
  private Long editorId;

  @NotNull
  @Schema(description = "comment title", example = "해당 사안 수정했습니다. 확인 부탁 드려요.")
  private String content;
}
