package com.api.backend.schedule.data.dto;

import com.api.backend.schedule.data.entity.Schedule;
import com.api.backend.schedule.data.entity.TeamParticipantsSchedule;
import com.api.backend.schedule.data.type.RepeatCycle;
import com.api.backend.team.data.type.TeamRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleResponse {

  private Long scheduleId;
  private Long categoryId;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime startDt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime endDt;
  private String title;
  private String content;
  private String place;
  private boolean isRepeat;
  private RepeatCycle repeatCycle;
  private List<Long> teamParticipantsIds;
  private List<String> teamParticipantsNames;
  private List<TeamRole> teamRoles;

  public static ScheduleResponse from(Schedule schedule) {
    return ScheduleResponse.builder()
        .scheduleId(schedule.getScheduleId())
        .categoryId(schedule.getScheduleCategory().getScheduleCategoryId())
        .startDt(schedule.getStartDt())
        .endDt(schedule.getEndDt())
        .title(schedule.getTitle())
        .content(schedule.getContent())
        .place(schedule.getPlace())
        .isRepeat(schedule.isRepeat())
        .repeatCycle(schedule.getRepeatCycle())
        .teamParticipantsIds(getTeamParticipantsIdsFromSchedules(schedule.getTeamParticipantsSchedules()))
        .teamParticipantsNames(getTeamParticipantsNameFromSchedules(schedule.getTeamParticipantsSchedules()))
        .teamRoles(getTeamParticipantsRoleFromSchedules(schedule.getTeamParticipantsSchedules()))
        .build();
  }

  public static Page<ScheduleResponse> from (Page<Schedule> schedules) {
    return schedules.map(ScheduleResponse::from);
  }

  public static List<Long> getTeamParticipantsIdsFromSchedules(List<TeamParticipantsSchedule> teamParticipantsSchedules) {
    List<Long> teamParticipantsIds = new ArrayList<>();
    if (teamParticipantsSchedules != null) {
      for (TeamParticipantsSchedule teamParticipantsSchedule : teamParticipantsSchedules) {
        teamParticipantsIds.add(teamParticipantsSchedule.getTeamParticipants().getTeamParticipantsId());
      }
    }
    return teamParticipantsIds;
  }

  public static List<String> getTeamParticipantsNameFromSchedules(List<TeamParticipantsSchedule> teamParticipantsSchedules) {
    List<String> teamParticipantsNames = new ArrayList<>();
    if (teamParticipantsSchedules != null) {
      for (TeamParticipantsSchedule teamParticipantsSchedule : teamParticipantsSchedules) {
        teamParticipantsNames.add(teamParticipantsSchedule.getTeamParticipants().getTeamNickName());
      }
    }
    return teamParticipantsNames;
  }

  public static List<TeamRole> getTeamParticipantsRoleFromSchedules(List<TeamParticipantsSchedule> teamParticipantsSchedules) {
    List<TeamRole> teamParticipantsRoles = new ArrayList<>();
    if (teamParticipantsSchedules != null) {
      for (TeamParticipantsSchedule teamParticipantsSchedule : teamParticipantsSchedules) {
        teamParticipantsRoles.add(teamParticipantsSchedule.getTeamParticipants().getTeamRole());
      }
    }
    return teamParticipantsRoles;
  }

}
