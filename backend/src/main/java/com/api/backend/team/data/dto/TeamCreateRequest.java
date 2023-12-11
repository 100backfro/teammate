package com.api.backend.team.data.dto;

import com.api.backend.team.data.annotation.ValidFile;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamCreateRequest {
  @NotBlank(message = "팀 이름이 존재하지 않습니다.")
  private String teamName;
  @ValidFile
  private MultipartFile teamImg;
  @Min(0)
  private int memberLimit;
}
