package com.api.backend.documents.data.dto;

import com.api.backend.comment.data.entity.Comment;
import com.api.backend.documents.data.entity.Documents;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {

  @NotBlank
  @Schema(description = "document id", example = "1")
  private String id;

  @NotBlank
  @Schema(description = "document title", example = "12월 10일 회의사항")
  private String title;

  @NotBlank
  @Schema(description = "document content", example = "안녕하세요. 2023/12/10 회의 사항들입니다. \n 내용을 모두 숙지해 주시길 바랍니다.")
  private String content;

  @NotNull
  @Schema(description = "document writer id", example = "문서를 작성한 팀참가자 id")
  private Long writerId;

  private Long modifierId;

  @NotNull
  @Schema(description = "document id", example = "12월 10일 회의사항")
  private Long teamId;

  private List<String> commentsId;

  @NotNull
  private LocalDateTime createdDt;
  @NotNull
  private LocalDateTime updatedDt;

  public static DocumentResponse from(Documents documents) {

    DocumentResponse documentResponse = DocumentResponse.builder()
        .id(documents.getId())
        .title(documents.getTitle())
        .content(documents.getContent())
        .writerId(documents.getWriterId())
        .modifierId(documents.getModifierId())
        .teamId(documents.getTeamId())
        .createdDt(documents.getCreatedDt())
        .updatedDt(documents.getUpdatedDt())
        .build();

    if (documents.getCommentIds() == null) {
        documentResponse.setCommentList(new ArrayList<>());
        return documentResponse;
    }

    List<String> commentIds = documents.getCommentIds().stream()
        .map(Comment::getId)
        .collect(Collectors.toList());
    documentResponse.setCommentList(commentIds);

    return documentResponse;
  }

  public void setCommentList(List<String> commentList) {
    if (commentsId == null) {
      commentsId = new ArrayList<>();
    }

    for (String commentId : commentList) {
      commentsId.add(commentId);
    }
  }
}