package com.api.backend.team.data.dto;

import static com.api.backend.team.data.ResponseMessage.DISBAND_TEAM;
import static com.api.backend.team.data.ResponseMessage.RESTORE_TEAM;

import com.api.backend.team.data.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class TeamRestoreResponse {
  private Long teamId;
  private String teamName;
  private String message;

  public static TeamRestoreResponse from(Team team) {
    return TeamRestoreResponse.builder()
        .teamId(team.getTeamId())
        .teamName(team.getName())
        .message(team.isDelete() ? DISBAND_TEAM : RESTORE_TEAM)
        .build();
  }
}
