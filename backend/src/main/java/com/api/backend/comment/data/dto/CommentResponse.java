package com.api.backend.comment.data.dto;

import com.api.backend.comment.data.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

  @NotBlank
  @Schema(description = "comment id", example = "6575d6fe99101a62f9710877")
  private String id;

  @NotBlank
  @Schema(description = "writer id", example = "12L")
  private Long writerId;

  @NotBlank
  @Schema(description = "comment title", example = "해당 사안 확인했습니다.")
  private String content;

  @NotBlank
  @Schema(description = "team id", example = "1L")
  private Long teamId;

  private LocalDateTime createdDt;
  private LocalDateTime updatedDt;

  public static CommentResponse from(Comment comment) {
    return CommentResponse.builder()
        .id(comment.getId())
        .content(comment.getContent())
        .writerId(comment.getWriterId())
        .teamId(comment.getTeamId())
        .createdDt(comment.getCreatedDt())
        .updatedDt(comment.getUpdatedDt())
        .build();
  }
}
