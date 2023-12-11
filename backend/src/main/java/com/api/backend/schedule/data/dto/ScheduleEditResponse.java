package com.api.backend.schedule.data.dto;

import com.api.backend.schedule.data.entity.Schedule;
import com.api.backend.schedule.data.type.RepeatCycle;
import com.api.backend.team.data.type.TeamRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleEditResponse {

  private Long scheduleId;
  private Long categoryId;
  private String title;
  private String content;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime startDt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime endDt;
  private String place;
  private boolean isRepeat;
  private RepeatCycle repeatCycle;
  private List<Long> teamParticipantsIds;
  private List<String> teamParticipantsNames;
  private List<TeamRole> teamRoles;

  public static ScheduleEditResponse from(Schedule schedule) {
    return ScheduleEditResponse.builder()
        .scheduleId(schedule.getScheduleId())
        .categoryId(schedule.getScheduleCategory().getScheduleCategoryId())
        .startDt(schedule.getStartDt())
        .endDt(schedule.getEndDt())
        .title(schedule.getTitle())
        .content(schedule.getContent())
        .place(schedule.getPlace())
        .isRepeat(schedule.isRepeat())
        .repeatCycle(schedule.getRepeatCycle())
        .teamParticipantsIds(ScheduleResponse.getTeamParticipantsIdsFromSchedules(schedule.getTeamParticipantsSchedules()))
        .teamParticipantsNames(ScheduleResponse.getTeamParticipantsNameFromSchedules(schedule.getTeamParticipantsSchedules()))
        .teamRoles(ScheduleResponse.getTeamParticipantsRoleFromSchedules(schedule.getTeamParticipantsSchedules()))
        .build();
  }
}
