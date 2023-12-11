package com.api.backend.team.data.dto;

import static com.api.backend.team.data.ResponseMessage.UPDATE_TEAM;

import com.api.backend.team.data.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamUpdateResponse {
  private Long teamId;
  private String teamName;
  private String profileUrl;
  private String message;

  public static TeamUpdateResponse from(Team team) {
    return TeamUpdateResponse.builder()
        .profileUrl(team.getProfileUrl())
        .message(UPDATE_TEAM)
        .teamName(team.getName())
        .teamId(team.getTeamId())
        .build();
  }
}
