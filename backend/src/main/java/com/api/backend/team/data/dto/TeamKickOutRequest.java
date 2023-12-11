package com.api.backend.team.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamKickOutRequest {
  private Long teamId;
  private Long userId;
  private String kickOutReason;
}
