package com.api.backend.team.data.dto;

import static com.api.backend.notification.data.NotificationMessage.getDisbandTeamName;
import static com.api.backend.team.data.ResponseMessage.DISBANDING_TEAM;

import com.api.backend.notification.data.type.AlarmType;
import com.api.backend.notification.transfers.MembersNotifyByDto;
import com.api.backend.team.data.entity.Team;
import java.time.LocalDate;
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
public class TeamDisbandResponse implements MembersNotifyByDto {
  private Long teamId;
  private String teamName;
  private LocalDate reservationDt;
  private String message;

  // 알람
  private Long excludeMemberId;

  public static TeamDisbandResponse from(Team team, Long memberId) {
    return TeamDisbandResponse.builder()
        .teamId(team.getTeamId())
        .excludeMemberId(memberId)
        .teamName(team.getName())
        .reservationDt(team.getRestorationDt())
        .message(DISBANDING_TEAM).build();
  }


  @Override
  public AlarmType getAlarmType() {
    return AlarmType.TEAM_DISBAND;
  }

  @Override
  public String getSendMessage() {
    return getDisbandTeamName(teamName);
  }
}
