package com.api.backend.schedule.data.dto;

import com.api.backend.schedule.data.entity.TeamParticipantsSchedule;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamParticipantsScheduleDto {
  private Long teamParticipantsScheduleId;
  private Long scheduleId;
  private Long teamParticipantsId;
  public static TeamParticipantsScheduleDto of(TeamParticipantsSchedule teamParticipantsSchedule) {
    return TeamParticipantsScheduleDto.builder()
        .teamParticipantsScheduleId(teamParticipantsSchedule.getTeamParticipantsScheduleId())
        .scheduleId(teamParticipantsSchedule.getSchedule().getScheduleId())
        .teamParticipantsId(teamParticipantsSchedule.getTeamParticipants().getTeamParticipantsId())
        .build();
  }

  public static List<TeamParticipantsScheduleDto> of(Page<TeamParticipantsSchedule> teamParticipantsSchedules) {
    if (teamParticipantsSchedules != null) {
      List<TeamParticipantsScheduleDto> teamParticipantsScheduleDtoList = new ArrayList<>();
      for (TeamParticipantsSchedule teamParticipantsSchedule : teamParticipantsSchedules) {
        teamParticipantsScheduleDtoList.add(TeamParticipantsScheduleDto.of(teamParticipantsSchedule));
      }
      return teamParticipantsScheduleDtoList;
    }
    return new ArrayList<>();
  }
}
