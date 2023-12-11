package com.api.backend.team.data.dto;

import com.api.backend.team.data.entity.Team;
import com.api.backend.team.service.file.LocalImgService;
import com.api.backend.team.service.file.impl.ImgStoreImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TeamCreateResponse {

  private Long teamId;
  private String teamName;
  private String teamUrl;
  private Long leaderId;
  private String inviteCode;
  public static TeamCreateResponse from(Team team,Long userId) {
    return TeamCreateResponse.builder()
        .teamId(team.getTeamId())
        .leaderId(userId)
        .teamUrl(team.getProfileUrl())
        .inviteCode(team.getInviteLink())
        .teamName(team.getName())
        .build();
  }
}
