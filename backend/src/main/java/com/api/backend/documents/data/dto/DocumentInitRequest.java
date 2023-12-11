package com.api.backend.documents.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DocumentInitRequest {
  @NotBlank(message = "문서 제목을 입력하세요.")
  @Schema(description = "document title", example = "12월 10일 회의 사항")
  private String title;

  @NotBlank(message = "문서 내용을 입력하세요.")
  @Schema(description = "document content", example = "안녕하세요. 2023/12/10 회의 사항들입니다. \n 내용을 모두 숙지해 주시길 바랍니다.")
  private String content;

  @NotBlank(message = "문서를 입력한 사용자의 email을 입력하세요.")
  @Schema(description = "document writer id", example = "member@example.com")
  private String writerEmail;

}
