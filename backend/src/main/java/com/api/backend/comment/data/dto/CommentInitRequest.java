package com.api.backend.comment.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentInitRequest {
  @NotNull
  @Schema(description = "writer id", example = "12L")
  private Long writerId;

  @NotBlank
  @Schema(description = "comment title", example = "해당 사안 확인했습니다.")
  private String content;
}
