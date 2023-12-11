package com.api.backend.team.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TeamKickOutResponse {
  private Long teamId;
  private Long kickOutMemberId;
  private String nickName;
  private String message;
}
