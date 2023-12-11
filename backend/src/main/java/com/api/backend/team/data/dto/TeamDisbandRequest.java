package com.api.backend.team.data.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamDisbandRequest {
  @NotNull(message = "teamId는 비어있는 값입니다.")
  private Long teamId;
  @NotNull(message = "비밀번호를 입력해주세요")
  private String password;
}
