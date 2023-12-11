package com.api.backend.schedule.service;

import static com.api.backend.global.exception.type.ErrorCode.INVALID_REPEAT_CYCLE_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.SCHEDULE_CATEGORY_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.SCHEDULE_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION;

import com.api.backend.category.data.entity.ScheduleCategory;
import com.api.backend.category.data.repository.ScheduleCategoryRepository;
import com.api.backend.category.type.CategoryType;
import com.api.backend.global.exception.CustomException;
import com.api.backend.global.exception.type.ErrorCode;
import com.api.backend.schedule.data.dto.ScheduleRequest;
import com.api.backend.schedule.data.dto.ScheduleResponse;
import com.api.backend.schedule.data.entity.Schedule;
import com.api.backend.schedule.data.entity.TeamParticipantsSchedule;
import com.api.backend.schedule.data.repository.ScheduleRepository;
import com.api.backend.schedule.data.repository.TeamParticipantsScheduleRepository;
import com.api.backend.team.data.entity.Team;
import com.api.backend.team.data.entity.TeamParticipants;
import com.api.backend.team.data.repository.TeamParticipantsRepository;
import com.api.backend.team.data.repository.TeamRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.temporal.TemporalAdjusters;

@Service
@AllArgsConstructor
public class ScheduleService {

  private final ScheduleRepository scheduleRepository;
  private final TeamRepository teamRepository;
  private final ScheduleCategoryRepository categoryRepository;
  private final TeamParticipantsRepository teamParticipantsRepository;
  private final TeamParticipantsScheduleRepository teamParticipantsScheduleRepository;

  public Page<Schedule> addSchedules(ScheduleRequest scheduleRequest) {
    Team team = findTeamOrElseThrow(scheduleRequest.getTeamId());
    ScheduleCategory category = findScheduleCategoryOrElseThrow(scheduleRequest.getCategoryId());
    List<Schedule> schedules;

    validateSameTeamOrElsThrow(scheduleRequest.getTeamParticipantsIds(), team);

    if (scheduleRequest.isRepeat()) {
      schedules = createRepeatingSchedules(scheduleRequest, team, category);
    } else {
      Schedule schedule = createSingleSchedule(scheduleRequest, team, category);
      schedules = Collections.singletonList(schedule);
    }
    return new PageImpl<>(schedules);
  }

  public Schedule searchScheduleDetailInfo(Long scheduleId, Long teamId) {
    findTeamOrElseThrow(teamId);
    return scheduleRepository.findScheduleByScheduleIdAndTeam_TeamId(scheduleId, teamId);
  }

  public Schedule editScheduleAndSave(ScheduleRequest editRequest) {
    Team team = findTeamOrElseThrow(editRequest.getTeamId());
    ScheduleCategory category = findScheduleCategoryOrElseThrow(editRequest.getCategoryId());

    validateSameTeamOrElsThrow(editRequest.getTeamParticipantsIds(), team);

    Schedule existingSchedule = scheduleRepository.findById(editRequest.getScheduleId())
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND_EXCEPTION));

    Schedule updatedSchedule = buildBaseSchedule(editRequest, team, category,
        editRequest.getStartDt());

    List<TeamParticipantsSchedule> existingTeamParticipantsSchedules =
        teamParticipantsScheduleRepository.findAllBySchedule_ScheduleId(
            existingSchedule.getScheduleId());

    List<TeamParticipantsSchedule> updatedTeamParticipantsSchedules =
        buildTeamParticipantsSchedules(updatedSchedule, editRequest.getTeamParticipantsIds());

    for (TeamParticipantsSchedule existingTeamParticipantsSchedule : existingTeamParticipantsSchedules) {
      TeamParticipants participants = existingTeamParticipantsSchedule.getTeamParticipants();

      if (editRequest.getTeamParticipantsIds().contains(participants.getTeamParticipantsId())) {
        updatedSchedule.getTeamParticipantsSchedules().add(existingTeamParticipantsSchedule);
      }
    }

    updatedSchedule.setTeamParticipantsSchedules(updatedTeamParticipantsSchedules);
    scheduleRepository.save(updatedSchedule);
    return updatedSchedule;
  }

  @Transactional
  public void deleteSchedule(Long scheduleId) {
    Schedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND_EXCEPTION));
    scheduleRepository.delete(schedule);
  }

  public Page<ScheduleResponse> getSchedulesForMonth(Long teamId, LocalDate monthStart,
      LocalDate monthEnd, CategoryType type, Pageable pageable) {
    LocalDateTime startDt = monthStart.atStartOfDay();
    LocalDateTime endDt = monthEnd.atTime(LocalTime.MAX);
    return getSchedulesByDateRange(teamId, startDt, endDt, type, pageable);
  }

  public Page<ScheduleResponse> getSchedulesForWeek(Long teamId, LocalDate startDt, LocalDate endDt,
      CategoryType type, Pageable pageable) {
    LocalDate weekStart = startDt.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDate weekEnd = startDt.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

    LocalDateTime startDateTime = weekStart.atTime(LocalTime.MIN);
    LocalDateTime endDateTime = weekEnd.atTime(LocalTime.MAX);

    return getSchedulesByDateRange(teamId, startDateTime, endDateTime, type, pageable);
  }


  private Schedule createSingleSchedule(ScheduleRequest scheduleRequest, Team team,
      ScheduleCategory category) {
    return createSchedulesAndSave(scheduleRequest, team, category, scheduleRequest.getStartDt());
  }

  private Schedule createSchedulesAndSave(ScheduleRequest scheduleRequest, Team team,
      ScheduleCategory category, LocalDateTime currentStart) {

    Schedule schedule = buildBaseSchedule(scheduleRequest, team, category, currentStart);

    List<TeamParticipantsSchedule> teamParticipantsSchedules = buildTeamParticipantsSchedules(
        schedule, scheduleRequest.getTeamParticipantsIds()
    );
    schedule.setTeamParticipantsSchedules(teamParticipantsSchedules);
    scheduleRepository.save(schedule);
    return schedule;
  }

  private List<Schedule> createRepeatingSchedules(ScheduleRequest scheduleRequest, Team team,
      ScheduleCategory category) {
    List<Schedule> schedules = new ArrayList<>();
    LocalDateTime currentStart = scheduleRequest.getStartDt();

    while (currentStart.isBefore(scheduleRequest.getEndDt())) {
      Schedule schedule = createSchedulesAndSave(scheduleRequest, team, category, currentStart);
      schedules.add(schedule);

      switch (scheduleRequest.getRepeatCycle()) {
        case WEEKLY:
          currentStart = currentStart.plus(1, ChronoUnit.WEEKS);
          break;
        case MONTHLY:
          currentStart = currentStart.plus(1, ChronoUnit.MONTHS);
          break;
        case YEARLY:
          currentStart = currentStart.plus(1, ChronoUnit.YEARS);
          break;
        default:
          throw new CustomException(INVALID_REPEAT_CYCLE_EXCEPTION);
      }
    }
    return schedules;
  }

  private Schedule buildBaseSchedule(ScheduleRequest scheduleRequest, Team team,
      ScheduleCategory category, LocalDateTime startDt) {

    return Schedule.builder()
        .scheduleId(scheduleRequest.getScheduleId())
        .team(team)
        .scheduleCategory(category)
        .title(scheduleRequest.getTitle())
        .content(scheduleRequest.getContent())
        .startDt(startDt)
        .endDt(startDt)
        .isRepeat(scheduleRequest.isRepeat())
        .repeatCycle(scheduleRequest.getRepeatCycle())
        .teamParticipantsSchedules(new ArrayList<>())
        .place(scheduleRequest.getPlace())
        .build();
  }

  private List<TeamParticipantsSchedule> buildTeamParticipantsSchedules(Schedule schedule,
      List<Long> teamParticipantsIds) {
    List<TeamParticipantsSchedule> teamParticipantsSchedules = new ArrayList<>();

    for (Long teamParticipantsId : teamParticipantsIds) {
      TeamParticipants participants = findTeamParticipantsOrElseThrow(teamParticipantsId);

      TeamParticipantsSchedule teamParticipantsSchedule = TeamParticipantsSchedule.builder()
          .teamParticipants(participants)
          .schedule(schedule)
          .build();

      teamParticipantsSchedule.setSchedule(schedule);
      teamParticipantsSchedule.setTeamParticipants(participants);
      teamParticipantsSchedules.add(teamParticipantsSchedule);
    }

    return teamParticipantsSchedules;
  }


  private Team findTeamOrElseThrow(Long teamId) {
    return teamRepository.findById(teamId)
        .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND_EXCEPTION));
  }

  private ScheduleCategory findScheduleCategoryOrElseThrow(Long categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(SCHEDULE_CATEGORY_NOT_FOUND_EXCEPTION));
  }

  private TeamParticipants findTeamParticipantsOrElseThrow(Long teamParticipantsId) {
    return teamParticipantsRepository.findById(teamParticipantsId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));
  }

  private void validateSameTeamOrElsThrow(List<Long> teamParticipantsIds, Team team) {
    for (Long teamParticipantsId : teamParticipantsIds) {
      TeamParticipants participants = findTeamParticipantsOrElseThrow(teamParticipantsId);
      if (!team.equals(participants.getTeam())) {
        throw new CustomException(ErrorCode.TEAM_PARTICIPANTS_NOT_VALID_EXCEPTION);
      }
    }
  }


  private Page<ScheduleResponse> getSchedulesByDateRange(Long teamId, LocalDateTime startDt,
      LocalDateTime endDt, CategoryType type, Pageable pageable) {
    Page<Schedule> schedules;

    if (type != null) {
      schedules = scheduleRepository.findByTeam_TeamIdAndStartDtBetweenAndScheduleCategory_CategoryType(
          teamId, startDt, endDt, type, pageable);
    } else {
      schedules = scheduleRepository.findByTeam_TeamIdAndStartDtBetween(teamId, startDt, endDt, pageable);
    }

    return ScheduleResponse.from(schedules);
  }

}
