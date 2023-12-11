package com.api.backend.team.data.dto;

import com.api.backend.team.data.entity.TeamParticipants;
import com.api.backend.team.data.type.TeamRole;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TeamParticipantsDto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long teamParticipantsId;
  private Long teamId;
  @Enumerated(EnumType.STRING)
  private TeamRole teamRole;
  private String participantsProfileUrl;
  private String teamNickName;

  public static TeamParticipantsDto from(TeamParticipants teamParticipants) {
    return TeamParticipantsDto.builder()
        .teamId(teamParticipants.getTeam().getTeamId())
        .teamParticipantsId(teamParticipants.getTeamParticipantsId())
        .teamNickName(teamParticipants.getTeamNickName())
        .teamRole(teamParticipants.getTeamRole())
        .participantsProfileUrl(teamParticipants.getParticipantsProfileUrl())
        .build();
  }
  public static Page<TeamParticipantsDto> fromDtos(Page<TeamParticipants> teamParticipantsPage){
    return teamParticipantsPage.map(TeamParticipantsDto::from);
  }
}
