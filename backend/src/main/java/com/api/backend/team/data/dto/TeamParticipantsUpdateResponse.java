package com.api.backend.team.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TeamParticipantsUpdateResponse {

  private Long teamId;
  private Long updateTeamParticipantId;
  private String updateTeamParticipantNickName;
  private String teamName;
  private String message;
}
