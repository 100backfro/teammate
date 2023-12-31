package com.api.backend.schedule.service;

import static com.api.backend.global.exception.type.ErrorCode.NON_REPEATING_SCHEDULE_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.SCHEDULE_CATEGORY_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.SCHEDULE_CREATOR_EXISTS_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.SCHEDULE_CREATOR_NOT_MATCH_TEAM_PARTICIPANTS_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.SCHEDULE_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_ID_DUPLICATE_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION;

import com.api.backend.category.data.entity.ScheduleCategory;
import com.api.backend.category.data.repository.ScheduleCategoryRepository;
import com.api.backend.category.type.CategoryType;
import com.api.backend.global.exception.CustomException;
import com.api.backend.global.exception.type.ErrorCode;
import com.api.backend.schedule.data.dto.AlarmScheduleDeleteResponse;
import com.api.backend.schedule.data.dto.AllSchedulesMonthlyView;
import com.api.backend.schedule.data.dto.RepeatScheduleInfoEditRequest;
import com.api.backend.schedule.data.dto.RepeatToSimpleScheduleEditRequest;
import com.api.backend.schedule.data.dto.ScheduleDeleteRequest;
import com.api.backend.schedule.data.dto.ScheduleRequest;
import com.api.backend.schedule.data.dto.SimpleScheduleInfoEditRequest;
import com.api.backend.schedule.data.dto.SimpleToRepeatScheduleEditRequest;
import com.api.backend.schedule.data.entity.RepeatSchedule;
import com.api.backend.schedule.data.entity.SimpleSchedule;
import com.api.backend.schedule.data.entity.TeamParticipantsSchedule;
import com.api.backend.schedule.data.repository.RepeatScheduleRepository;
import com.api.backend.schedule.data.repository.SimpleScheduleRepository;
import com.api.backend.schedule.data.repository.TeamParticipantsScheduleRepository;
import com.api.backend.schedule.data.type.EditOption;
import com.api.backend.schedule.data.type.RepeatCycle;
import com.api.backend.team.data.entity.Team;
import com.api.backend.team.data.entity.TeamParticipants;
import com.api.backend.team.data.repository.TeamParticipantsRepository;
import com.api.backend.team.data.repository.TeamRepository;
import com.api.backend.team.data.type.TeamRole;
import com.api.backend.team.service.TeamParticipantsService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ScheduleService {

  private final SimpleScheduleRepository simpleScheduleRepository;
  private final RepeatScheduleRepository repeatScheduleRepository;
  private final TeamRepository teamRepository;
  private final ScheduleCategoryRepository categoryRepository;
  private final TeamParticipantsRepository teamParticipantsRepository;
  private final TeamParticipantsScheduleRepository teamParticipantsScheduleRepository;
  private final TeamParticipantsService teamParticipantsService;

  @Transactional
  public SimpleSchedule addSimpleScheduleAndSave(ScheduleRequest scheduleRequest, Long memberId) {
    validateTeamParticipant(scheduleRequest.getTeamId(), memberId);

    Team team = findTeamOrElseThrow(scheduleRequest.getTeamId());
    ScheduleCategory category = findScheduleCategoryOrElseThrow(scheduleRequest.getCategoryId());
    SimpleSchedule simpleSchedule = buildSimpleScheduleForAdd(scheduleRequest, team, category);

    List<TeamParticipantsSchedule> teamParticipantsSchedules = buildTeamParticipantsSchedulesBySimpleSchedule(
        simpleSchedule, scheduleRequest.getTeamParticipantsIds()
    );

    simpleSchedule.setTeamParticipantsSchedules(teamParticipantsSchedules);
    simpleScheduleRepository.save(simpleSchedule);
    log.info("단순 일정이 성공적으로 저장되었습니다.");
    return simpleSchedule;
  }

  @Transactional
  public RepeatSchedule addRepeatScheduleAndSave(ScheduleRequest scheduleRequest, Long memberId) {
    validateTeamParticipant(scheduleRequest.getTeamId(), memberId);

    Team team = findTeamOrElseThrow(scheduleRequest.getTeamId());
    ScheduleCategory category = findScheduleCategoryOrElseThrow(scheduleRequest.getCategoryId());
    List<Long> teamParticipantsIds = scheduleRequest.getTeamParticipantsIds();

    validateSameTeamOrElsThrow(teamParticipantsIds, team.getTeamId());

    RepeatSchedule repeatSchedule = buildRepeatScheduleForAdd(scheduleRequest, team, category);

    List<TeamParticipantsSchedule> teamParticipantsSchedules = buildTeamParticipantsSchedulesByRepeatSchedule(
        repeatSchedule, scheduleRequest.getTeamParticipantsIds()
    );

    repeatSchedule.setTeamParticipantsSchedules(teamParticipantsSchedules);
    repeatScheduleRepository.save(repeatSchedule);
    log.info("반복 일정이 성공적으로 저장되었습니다.");
    return repeatSchedule;
  }

  @Transactional
  public SimpleSchedule editSimpleScheduleInfoAndSave(SimpleScheduleInfoEditRequest editRequest, Long memberId) {
    validateTeamParticipant(editRequest.getTeamId(), memberId);

    Team team = findTeamOrElseThrow(editRequest.getTeamId());
    ScheduleCategory category = findScheduleCategoryOrElseThrow(editRequest.getCategoryId());
    List<Long> teamParticipantsIds = editRequest.getTeamParticipantsIds();

    validateSameTeamOrElsThrow(teamParticipantsIds, team.getTeamId());

    SimpleSchedule updatedSimpleSchedule = findSimpleScheduleOrElseThrow(
        editRequest.getSimpleScheduleId()
    );

    updatedSimpleSchedule.setSimpleScheduleInfo(
        category,
        editRequest.getTitle(), editRequest.getContent(),
        editRequest.getStartDt(), editRequest.getEndDt(),
        editRequest.getPlace(), editRequest.getColor()
    );

    List<TeamParticipantsSchedule> originTeamParticipantsSchedules =
        teamParticipantsScheduleRepository.findAllBySimpleSchedule_SimpleScheduleId(
            updatedSimpleSchedule.getSimpleScheduleId()
        );

    teamParticipantsScheduleRepository.deleteAll(originTeamParticipantsSchedules);

    List<TeamParticipantsSchedule> teamParticipantsSchedules = buildTeamParticipantsSchedulesBySimpleSchedule(
        updatedSimpleSchedule, teamParticipantsIds);

    updatedSimpleSchedule.setTeamParticipantsSchedules(teamParticipantsSchedules);
    simpleScheduleRepository.save(updatedSimpleSchedule);
    log.info("단순 일정 정보가 성공적으로 수정되었습니다.");
    return updatedSimpleSchedule;
  }


  @Transactional
  public SimpleSchedule convertRepeatToSimpleSchedule(RepeatToSimpleScheduleEditRequest editRequest, Long memberId) {
    validateTeamParticipant(editRequest.getTeamId(), memberId);

    Team team = findTeamOrElseThrow(editRequest.getTeamId());
    ScheduleCategory category = findScheduleCategoryOrElseThrow(editRequest.getCategoryId());
    List<Long> teamParticipantsIds = editRequest.getTeamParticipantsIds();

    validateSameTeamOrElsThrow(teamParticipantsIds, team.getTeamId());

    RepeatSchedule repeatSchedule = findRepeatScheduleOrElseThrow(
        editRequest.getRepeatScheduleId());

    Long originCreateParticipantId = repeatSchedule.getCreateParticipantId();

    SimpleSchedule simpleSchedule = SimpleSchedule.builder()
        .team(team)
        .scheduleCategory(category)
        .title(editRequest.getTitle())
        .content(editRequest.getContent())
        .startDt(editRequest.getStartDt())
        .endDt(editRequest.getEndDt())
        .place(editRequest.getPlace())
        .color(editRequest.getColor())
        .createParticipantId(originCreateParticipantId)
        .build();

    List<TeamParticipantsSchedule> teamParticipantsSchedules = buildTeamParticipantsSchedulesBySimpleSchedule(
        simpleSchedule, editRequest.getTeamParticipantsIds()
    );

    simpleSchedule.setTeamParticipantsSchedules(teamParticipantsSchedules);
    repeatScheduleRepository.delete(repeatSchedule);
    simpleScheduleRepository.save(simpleSchedule);
    log.info("일정 타입이 성공적으로 변경되었습니다. (반복 일정 -> 단순 일정)");
    return simpleSchedule;
  }

  @Transactional
  public RepeatSchedule convertSimpleToRepeatSchedule(SimpleToRepeatScheduleEditRequest editRequest, Long memberId) {
    validateTeamParticipant(editRequest.getTeamId(), memberId);

    Team team = findTeamOrElseThrow(editRequest.getTeamId());
    ScheduleCategory category = findScheduleCategoryOrElseThrow(editRequest.getCategoryId());
    List<Long> teamParticipantsIds = editRequest.getTeamParticipantsIds();

    validateSameTeamOrElsThrow(teamParticipantsIds, team.getTeamId());

    String month = editRequest.getStartDt().getMonth().name();
    int day = editRequest.getStartDt().getDayOfMonth();
    String dayOfWeek = String.valueOf(editRequest.getStartDt().getDayOfWeek());

    SimpleSchedule simpleSchedule = findSimpleScheduleOrElseThrow(
        editRequest.getSimpleScheduleId());

    Long originCreateParticipantId = simpleSchedule.getCreateParticipantId();

    RepeatSchedule repeatSchedule = RepeatSchedule.builder()
        .scheduleCategory(category)
        .team(team)
        .title(editRequest.getTitle())
        .content(editRequest.getContent())
        .place(editRequest.getPlace())
        .startDt(editRequest.getStartDt())
        .endDt(editRequest.getEndDt())
        .repeatCycle(editRequest.getRepeatCycle())
        .color(editRequest.getColor())
        .createParticipantId(originCreateParticipantId)
        .build();

    setRepeatScheduleFieldsByCycle(repeatSchedule, month, day, dayOfWeek, editRequest.getRepeatCycle());

    List<TeamParticipantsSchedule> teamParticipantsSchedules = buildTeamParticipantsSchedulesByRepeatSchedule(
        repeatSchedule, editRequest.getTeamParticipantsIds()
    );

    repeatSchedule.setTeamParticipantsSchedules(teamParticipantsSchedules);

    simpleScheduleRepository.delete(simpleSchedule);
    log.info("일정 타입이 성공적으로 변경되었습니다. (단순 일정 -> 반복 일정)");
    repeatScheduleRepository.save(repeatSchedule);
    return repeatSchedule;
  }

  @Transactional
  public RepeatSchedule editRepeatScheduleInfoAndSave(RepeatScheduleInfoEditRequest editRequest, Long memberId) {
    validateTeamParticipant(editRequest.getTeamId(), memberId);

    Team team = findTeamOrElseThrow(editRequest.getTeamId());
    ScheduleCategory category = findScheduleCategoryOrElseThrow(editRequest.getCategoryId());
    List<Long> teamParticipantsIds = editRequest.getTeamParticipantsIds();

    validateSameTeamOrElsThrow(teamParticipantsIds, team.getTeamId());

    String month = editRequest.getStartDt().getMonth().name();
    int day = editRequest.getStartDt().getDayOfMonth();
    String dayOfWeek = String.valueOf(editRequest.getStartDt().getDayOfWeek());

    RepeatSchedule originRepeatSchedule = repeatScheduleRepository.findByOriginRepeatScheduleId(
        editRequest.getRepeatScheduleId()
    );

    Long originCreateParticipantId = originRepeatSchedule.getCreateParticipantId();

    if (editRequest.getEditOption().equals(EditOption.ALL_SCHEDULES)) {

      if (originRepeatSchedule != null) {
        repeatScheduleRepository.delete(originRepeatSchedule);
      }

      RepeatSchedule updateRepeatSchedule = findRepeatScheduleOrElseThrow(
          editRequest.getRepeatScheduleId()
      );

      updateRepeatSchedule.setRepeatScheduleInfo(
          category, editRequest.getTitle(), editRequest.getContent(),
          editRequest.getStartDt(), editRequest.getEndDt(),
          editRequest.getPlace(), editRequest.getColor()
      );

      setRepeatScheduleFieldsByCycle(
          updateRepeatSchedule, month, day, dayOfWeek,
          editRequest.getRepeatCycle()
      );

      List<TeamParticipantsSchedule> originTeamParticipantsSchedules =
          teamParticipantsScheduleRepository.findAllByRepeatSchedule_RepeatScheduleId(updateRepeatSchedule.getRepeatScheduleId());

      teamParticipantsScheduleRepository.deleteAll(originTeamParticipantsSchedules);

      List<TeamParticipantsSchedule> teamParticipantsSchedules = buildTeamParticipantsSchedulesByRepeatSchedule(
          updateRepeatSchedule, teamParticipantsIds
      );

      updateRepeatSchedule.setTeamParticipantsSchedules(teamParticipantsSchedules);
      repeatScheduleRepository.save(updateRepeatSchedule);
      log.info("반복 일정의 모든 일정이 성공적으로 수정되었습니다.");
      return updateRepeatSchedule;

    } else {
      RepeatSchedule updateRepeatSchedule = buildRepeatScheduleForEdit(editRequest, team, category, originCreateParticipantId);

      List<TeamParticipantsSchedule> originTeamParticipantsSchedules =
          teamParticipantsScheduleRepository.findAllByRepeatSchedule_RepeatScheduleId(
              updateRepeatSchedule.getRepeatScheduleId()
          );

      teamParticipantsScheduleRepository.deleteAll(originTeamParticipantsSchedules);

      List<TeamParticipantsSchedule> teamParticipantsSchedules = buildTeamParticipantsSchedulesByRepeatSchedule(
          updateRepeatSchedule, teamParticipantsIds
      );

      updateRepeatSchedule.setTeamParticipantsSchedules(teamParticipantsSchedules);
      updateRepeatSchedule.setOriginRepeatScheduleId(originRepeatSchedule.getOriginRepeatScheduleId());
      repeatScheduleRepository.save(updateRepeatSchedule);
      log.info("반복 일정의 이 일정 혹은 이 일정 및 향후 일정이 성공적으로 수정되었습니다.");
      return updateRepeatSchedule;
    }
  }


  @Transactional
  public AlarmScheduleDeleteResponse deleteSimpleSchedule(ScheduleDeleteRequest deleteRequest, Long memberId) {
    SimpleSchedule simpleSchedule = simpleScheduleRepository.findById(deleteRequest.getScheduleId())
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND_EXCEPTION));

    TeamParticipants teamParticipants = teamParticipantsService.getTeamParticipant(
        deleteRequest.getTeamId(), memberId);

    List<Long> teamParticipantsIds = simpleSchedule.getTeamParticipantsSchedules()
        .stream().map(TeamParticipantsSchedule::getTeamParticipants)
        .map(TeamParticipants::getTeamParticipantsId)
        .collect(Collectors.toList());

    if (teamParticipants.getTeamRole() == TeamRole.LEADER
        && simpleSchedule.getCreateParticipantId() != teamParticipants.getTeamParticipantsId()
    ) {
      if (teamParticipantsRepository.existsByTeamParticipantsId(
          simpleSchedule.getCreateParticipantId())) {
        throw new CustomException(SCHEDULE_CREATOR_EXISTS_EXCEPTION);
      }
    } else {
      if (deleteRequest.getTeamParticipantId() != teamParticipants.getTeamParticipantsId()) {
        throw new CustomException(SCHEDULE_CREATOR_NOT_MATCH_TEAM_PARTICIPANTS_EXCEPTION);
      }
    }

    simpleScheduleRepository.delete(simpleSchedule);
    log.info("단순 일정이 성공적으로 삭제되었습니다.");
    return AlarmScheduleDeleteResponse.builder()
        .teamParticipantsId(teamParticipants.getTeamParticipantsId())
        .teamParticipantIds(teamParticipantsIds)
        .title(simpleSchedule.getTitle())
        .message("해당 단순 일정이 정상적으로 삭제되었습니다.")
        .build();
  }

  @Transactional
  public AlarmScheduleDeleteResponse deleteRepeatSchedule(ScheduleDeleteRequest deleteRequest, Long memberId) {
    RepeatSchedule repeatSchedule = repeatScheduleRepository.findById(deleteRequest.getScheduleId())
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND_EXCEPTION));

    TeamParticipants teamParticipants = teamParticipantsService.getTeamParticipant(
        deleteRequest.getTeamId(), memberId);

    List<Long> teamParticipantsIds = repeatSchedule.getTeamParticipantsSchedules()
        .stream().map(TeamParticipantsSchedule::getTeamParticipants)
        .map(TeamParticipants::getTeamParticipantsId)
        .collect(Collectors.toList());

    if (teamParticipants.getTeamRole() == TeamRole.LEADER
        && repeatSchedule.getCreateParticipantId() != teamParticipants.getTeamParticipantsId()
    ) {
      if (teamParticipantsRepository.existsByTeamParticipantsId(
          repeatSchedule.getCreateParticipantId())) {
        throw new CustomException(SCHEDULE_CREATOR_EXISTS_EXCEPTION);
      }
    } else {
      if (deleteRequest.getTeamParticipantId() != teamParticipants.getTeamParticipantsId()) {
        throw new CustomException(SCHEDULE_CREATOR_NOT_MATCH_TEAM_PARTICIPANTS_EXCEPTION);
      }
    }

    repeatScheduleRepository.delete(repeatSchedule);
    log.info("반복 일정이 성공적으로 삭제되었습니다.");
    return AlarmScheduleDeleteResponse.builder()
        .teamParticipantsId(teamParticipants.getTeamParticipantsId())
        .teamParticipantIds(teamParticipantsIds)
        .title(repeatSchedule.getTitle())
        .message("해당 반복 일정이 정상적으로 삭제되었습니다.")
        .build();
  }

  public SimpleSchedule getSimpleScheduleDetailInfo(Long scheduleId, Long teamId, Long memberId) {
    findTeamOrElseThrow(teamId);
    validateTeamParticipant(teamId, memberId);
    SimpleSchedule simpleSchedule = simpleScheduleRepository.findSimpleScheduleBySimpleScheduleIdAndTeam_TeamId(scheduleId, teamId);
    log.info("단순 일정 상세 조회에 성공하였습니다.");
    return simpleSchedule;
  }

  public RepeatSchedule getRepeatScheduleDetailInfo(Long scheduleId, Long teamId, Long memberId) {
    findTeamOrElseThrow(teamId);
    validateTeamParticipant(teamId, memberId);
    RepeatSchedule repeatSchedule = repeatScheduleRepository.findRepeatScheduleByRepeatScheduleIdAndTeam_TeamId(scheduleId, teamId);
    log.info("반복 일정 상세 조회에 성공하였습니다.");
    return repeatSchedule;
  }


  public List<AllSchedulesMonthlyView> getCategoryTypeMonthlySchedules(
      Long teamId, CategoryType categoryType, Long memberId
  ) {
    validateTeamParticipant(teamId, memberId);

    List<RepeatSchedule> repeatSchedules = repeatScheduleRepository.findAllByScheduleCategory_CategoryTypeAndTeam_TeamId(categoryType, teamId);
    List<SimpleSchedule> simpleSchedules = simpleScheduleRepository.findAllByScheduleCategory_CategoryTypeAndTeam_TeamId(categoryType, teamId);

    List<AllSchedulesMonthlyView> allSchedulesList = Stream.concat(
            repeatSchedules.stream().map(AllSchedulesMonthlyView::from),
            simpleSchedules.stream().map(AllSchedulesMonthlyView::from))
        .collect(Collectors.toList());

    log.info("카테고리 유형별 월간 보기 조회에 성공하였습니다.");
    return allSchedulesList;
  }

  public List<AllSchedulesMonthlyView> getAllMonthlySchedules(Long teamId, Long memberId) {
    validateTeamParticipant(teamId, memberId);

    List<RepeatSchedule> repeatSchedules = repeatScheduleRepository.findAllByTeam_TeamId(teamId);
    List<SimpleSchedule> simpleSchedules = simpleScheduleRepository.findAllByTeam_TeamId(teamId);

    List<AllSchedulesMonthlyView> allSchedulesList = Stream.concat(
        repeatSchedules.stream().map(AllSchedulesMonthlyView::from),
        simpleSchedules.stream().map(AllSchedulesMonthlyView::from))
        .collect(Collectors.toList());

    log.info("월간 보기 조회에 성공하였습니다.");
    return allSchedulesList;
  }

  private SimpleSchedule findSimpleScheduleOrElseThrow(Long simpleScheduleId) {
    return simpleScheduleRepository.findById(simpleScheduleId)
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND_EXCEPTION));
  }

  private RepeatSchedule findRepeatScheduleOrElseThrow(Long repeatScheduleId) {
    return repeatScheduleRepository.findById(repeatScheduleId)
        .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND_EXCEPTION));
  }

  private Team findTeamOrElseThrow(Long teamId) {
    return teamRepository.findById(teamId)
        .orElseThrow(() -> new CustomException(TEAM_NOT_FOUND_EXCEPTION));
  }

  private ScheduleCategory findScheduleCategoryOrElseThrow(Long categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(SCHEDULE_CATEGORY_NOT_FOUND_EXCEPTION));
  }

  private TeamParticipants findTeamParticipantsOrElseThrow(Long teamParticipantsId) {
    return teamParticipantsRepository.findById(teamParticipantsId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));
  }


  private void validateSameTeamOrElsThrow(List<Long> teamParticipantsIds, Long teamId) {
    for (Long teamParticipantsId : teamParticipantsIds) {
      TeamParticipants participants = findTeamParticipantsOrElseThrow(teamParticipantsId);
      if (teamId != participants.getTeam().getTeamId()) {
        throw new CustomException(ErrorCode.TEAM_PARTICIPANTS_NOT_VALID_EXCEPTION);
      }
    }
  }

  private void validateTeamParticipant(Long teamId, Long memberId) {
    teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(teamId, memberId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));
  }

  private SimpleSchedule buildSimpleScheduleForAdd(ScheduleRequest request, Team team, ScheduleCategory category) {
    return SimpleSchedule.builder()
        .team(team)
        .scheduleCategory(category)
        .title(request.getTitle())
        .content(request.getContent())
        .startDt(request.getStartDt())
        .endDt(request.getEndDt())
        .color(request.getColor())
        .teamParticipantsSchedules(new ArrayList<>())
        .place(request.getPlace())
        .createParticipantId(request.getCreateParticipantId())
        .build();
  }

  private RepeatSchedule buildRepeatScheduleForAdd(ScheduleRequest request, Team team, ScheduleCategory category) {
    String month = request.getStartDt().getMonth().name();
    int day = request.getStartDt().getDayOfMonth();
    String dayOfWeek = String.valueOf(request.getStartDt().getDayOfWeek());

    RepeatSchedule newRepeatSchedule = RepeatSchedule.builder()
        .scheduleCategory(category)
        .team(team)
        .title(request.getTitle())
        .content(request.getContent())
        .place(request.getPlace())
        .startDt(request.getStartDt())
        .endDt(request.getEndDt())
        .repeatCycle(request.getRepeatCycle())
        .color(request.getColor())
        .createParticipantId(request.getCreateParticipantId())
        .build();

    setRepeatScheduleFieldsByCycle(newRepeatSchedule, month, day, dayOfWeek,
        request.getRepeatCycle()
    );
    return newRepeatSchedule;
  }

  private RepeatSchedule buildRepeatScheduleForEdit(
      RepeatScheduleInfoEditRequest editRequest,
      Team team,
      ScheduleCategory category,
      Long originCreateParticipantId
  ) {
    String month = editRequest.getStartDt().getMonth().name();
    int day = editRequest.getStartDt().getDayOfMonth();
    String dayOfWeek = String.valueOf(editRequest.getStartDt().getDayOfWeek());

    RepeatSchedule newRepeatSchedule = RepeatSchedule.builder()
        .scheduleCategory(category)
        .team(team)
        .title(editRequest.getTitle())
        .content(editRequest.getContent())
        .place(editRequest.getPlace())
        .startDt(editRequest.getStartDt())
        .endDt(editRequest.getEndDt())
        .repeatCycle(editRequest.getRepeatCycle())
        .color(editRequest.getColor())
        .createParticipantId(originCreateParticipantId)
        .build();

    setRepeatScheduleFieldsByCycle(newRepeatSchedule, month, day, dayOfWeek,
        editRequest.getRepeatCycle()
    );
    return newRepeatSchedule;
  }

  private List<TeamParticipantsSchedule> buildTeamParticipantsSchedulesBySimpleSchedule(
      SimpleSchedule simpleSchedule,
      List<Long> teamParticipantsIds
  ) {
    if (hasDuplicateTeamParticipantsIds(teamParticipantsIds)) {
      throw new CustomException(TEAM_PARTICIPANTS_ID_DUPLICATE_EXCEPTION);
    }

    List<TeamParticipantsSchedule> teamParticipantsSchedules = new ArrayList<>();

    for (Long teamParticipantsId : teamParticipantsIds) {
      TeamParticipants participants = findTeamParticipantsOrElseThrow(teamParticipantsId);
      TeamParticipantsSchedule teamParticipantsSchedule = TeamParticipantsSchedule.builder()
          .teamParticipants(participants)
          .simpleSchedule(simpleSchedule)
          .build();
      teamParticipantsSchedule.setSimpleSchedule(simpleSchedule);
      teamParticipantsSchedule.setTeamParticipants(participants);
      teamParticipantsSchedules.add(teamParticipantsSchedule);
    }

    return teamParticipantsSchedules;
  }

  private List<TeamParticipantsSchedule> buildTeamParticipantsSchedulesByRepeatSchedule(
      RepeatSchedule repeatSchedule,
      List<Long> teamParticipantsIds
  ) {
    if (hasDuplicateTeamParticipantsIds(teamParticipantsIds)) {
      throw new CustomException(TEAM_PARTICIPANTS_ID_DUPLICATE_EXCEPTION);
    }

    List<TeamParticipantsSchedule> teamParticipantsSchedules = new ArrayList<>();

    for (Long teamParticipantsId : teamParticipantsIds) {
      TeamParticipants participants = findTeamParticipantsOrElseThrow(teamParticipantsId);
      TeamParticipantsSchedule teamParticipantsSchedule = TeamParticipantsSchedule.builder()
          .teamParticipants(participants)
          .repeatSchedule(repeatSchedule)
          .build();
      teamParticipantsSchedule.setRepeatSchedule(repeatSchedule);
      teamParticipantsSchedule.setTeamParticipants(participants);
      teamParticipantsSchedules.add(teamParticipantsSchedule);
    }

    return teamParticipantsSchedules;
  }

  private void setRepeatScheduleFieldsByCycle(
      RepeatSchedule repeatSchedule, String month, int day,
      String dayOfWeek, RepeatCycle repeatCycle
  ) {
    switch (repeatCycle) {
      case WEEKLY:
        repeatSchedule.setDayOfWeek(dayOfWeek);
        break;
      case MONTHLY:
        repeatSchedule.setDay(day);
        break;
      case YEARLY:
        repeatSchedule.setMonth(month);
        repeatSchedule.setDay(day);
        break;
      default:
        throw new CustomException(NON_REPEATING_SCHEDULE_EXCEPTION);
    }
  }

  private boolean hasDuplicateTeamParticipantsIds(List<Long> teamParticipantsIds) {
    Set<Long> uniqueIds = new HashSet<>();
    for (Long teamParticipantsId : teamParticipantsIds) {
      if (!uniqueIds.add(teamParticipantsId)) {
        return true;
      }
    }
    return false;
  }
}
