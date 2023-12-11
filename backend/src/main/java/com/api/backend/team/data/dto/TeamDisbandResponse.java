package com.api.backend.team.data.dto;

import static com.api.backend.team.data.ResponseMessage.DISBANDING_TEAM;

import com.api.backend.team.data.entity.Team;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
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
public class TeamDisbandResponse {
  @NotNull(message = "teamId는 비어있는 값입니다.")
  private Long teamId;
  private LocalDate reservationDt;
  @NotNull(message = "비밀번호를 입력해주세요")
  private String message;


  public static TeamDisbandResponse from(Team team) {
    return TeamDisbandResponse.builder()
        .teamId(team.getTeamId())
        .reservationDt(team.getRestorationDt())
        .message(DISBANDING_TEAM).build();
  }
}
