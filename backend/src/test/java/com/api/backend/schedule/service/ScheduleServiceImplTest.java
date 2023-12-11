package com.api.backend.schedule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.api.backend.category.data.entity.ScheduleCategory;
import com.api.backend.category.data.repository.ScheduleCategoryRepository;
import com.api.backend.global.exception.CustomException;
import com.api.backend.schedule.data.dto.ScheduleRequest;
import com.api.backend.schedule.data.entity.Schedule;
import com.api.backend.schedule.data.repository.ScheduleRepository;
import com.api.backend.schedule.data.type.RepeatCycle;
import com.api.backend.team.data.entity.Team;
import com.api.backend.team.data.repository.TeamParticipantsRepository;
import com.api.backend.team.data.repository.TeamRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

//TODO: api 완성 후 테스트 코드 구현 예정
@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

  @Mock
  private TeamRepository teamRepository;

  @Mock
  private ScheduleCategoryRepository categoryRepository;

  @Mock
  private ScheduleRepository scheduleRepository;

  @Mock
  private TeamParticipantsRepository teamParticipantsRepository;

  @InjectMocks
  private ScheduleService scheduleService;

  @Test
  @DisplayName("스케줄 추가 성공")
  public void addScheduleSuccess() {
    // Given
    Long teamId = 1L;
    Long categoryId = 1L;
    LocalDateTime now = LocalDateTime.now();
    ScheduleRequest scheduleRequest = ScheduleRequest.builder()
        .scheduleId(1L)
        .categoryId(categoryId)
        .teamId(teamId)
        .title("김하나 휴가")
        .content("휴가")
        .repeatCycle(RepeatCycle.YEARLY)
        .isRepeat(false)
        .place("집")
        .startDt(now)
        .endDt(now)
        .teamParticipantsIds(new ArrayList<>())
        .build();

    Team mockTeam = Team.builder()
        .teamId(teamId)
        .build();
    ScheduleCategory scheduleCategory = ScheduleCategory.builder()
        .scheduleCategoryId(categoryId)
        .build();

    when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(scheduleCategory));
    when(scheduleRepository.save(any(Schedule.class))).thenAnswer(
        invocation -> {
          Schedule savedSchedule = invocation.getArgument(0);
          assertNotNull(savedSchedule.getStartDt(), "스케줄 시작일은 null일 수 없습니다.");
          assertNotNull(savedSchedule.getEndDt(), "스케줄 종료일은 null일 수 없습니다.");
          return savedSchedule;
        });

    // When
    Page<Schedule> resultPage = scheduleService.addSchedules(scheduleRequest);

    // Then
    assertEquals(1, resultPage.getTotalElements(),
        "페이지에 포함된 스케줄의 총 개수는 1이어야 합니다.");

    Schedule result = resultPage.getContent().get(0);
    assertEquals(scheduleRequest.getTitle(), result.getTitle());
    assertEquals(scheduleRequest.getContent(), result.getContent());
    assertEquals(scheduleRequest.getStartDt(), result.getStartDt());
    assertEquals(scheduleRequest.getEndDt(), result.getEndDt());
    assertEquals(scheduleRequest.getRepeatCycle(), result.getRepeatCycle());
    assertEquals(scheduleRequest.isRepeat(), result.isRepeat());
    assertEquals(mockTeam, result.getTeam());
    assertEquals(scheduleCategory, result.getScheduleCategory());

    verify(teamRepository, times(1)).findById(teamId);
    verify(categoryRepository, times(1)).findById(categoryId);
    verify(scheduleRepository, times(1)).save(any(Schedule.class));
  }

//TODO: 스케줄 서비스 리팩토링 후 다시 구현 예정
//  @Test
//  @DisplayName("일정 조회 - 성공")
//  public void searchScheduleSuccess() {
//    // Given
//    Pageable pageable = Pageable.unpaged();
//
//    List<Schedule> mockScheduleList = new ArrayList<>();
//
//    ScheduleCategory scheduleCategory = ScheduleCategory.builder()
//        .scheduleCategoryId(1L)
//        .build();
//
//    Team team = Team.builder()
//        .teamId(1L)
//        .build();
//
//    LocalDateTime startDt = LocalDateTime.now();
//
//    Schedule schedule1 = Schedule.builder()
//        .scheduleId(1L)
//        .scheduleCategory(scheduleCategory)
//        .title("Test1")
//        .content("Test1")
//        .startDt(startDt)
//        .endDt(startDt.plusWeeks(1))
//        .team(team)
//        .isRepeat(false)
//        .repeatCycle(null)
//        .build();
//
//    Schedule schedule2 = Schedule.builder()
//        .scheduleId(2L)
//        .scheduleCategory(scheduleCategory)
//        .title("Test2")
//        .content("Test2")
//        .startDt(startDt)
//        .endDt(startDt.plusWeeks(1))
//        .team(team)
//        .isRepeat(false)
//        .repeatCycle(null)
//        .build();
//
//    mockScheduleList.add(schedule1);
//    mockScheduleList.add(schedule2);
//
//    Page<Schedule> mockPage = new PageImpl<>(mockScheduleList, pageable, mockScheduleList.size());
//
//    when(scheduleRepository.findAll(any(Pageable.class))).thenReturn(mockPage);
//
//    // When
//    Page<Schedule> resultPage = scheduleService.searchSchedule( team.getTeamId());
//
//    // Then
//    List<Schedule> resultScheduleList = resultPage.getContent();
//    assertEquals(mockScheduleList.size(), resultScheduleList.size(),
//        "결과 스케줄 목록의 크기는 예상과 일치해야 합니다.");
//  }


  @Test
  @DisplayName("일정 수정 - 성공")
  void editScheduleSuccess() {
    // Given
    Long teamId = 1L;
    Long categoryId = 1L;
    Long scheduleId = 1L;

    ScheduleRequest request = ScheduleRequest.builder()
        .scheduleId(scheduleId)
        .categoryId(categoryId)
        .teamId(teamId)
        .title("김하나 휴가")
        .content("휴가")
        .repeatCycle(null)
        .isRepeat(false)
        .place("집")
        .startDt(LocalDateTime.now())
        .endDt(LocalDateTime.now())
        .teamParticipantsIds(new ArrayList<>())
        .build();

    Team mockTeam = Team.builder()
        .teamId(teamId)
        .build();

    ScheduleCategory scheduleCategory = ScheduleCategory.builder()
        .scheduleCategoryId(categoryId)
        .build();

    when(scheduleRepository.findById(scheduleId))
        .thenReturn(Optional.of(Schedule.builder().build()));

    when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(scheduleCategory));
    when(scheduleRepository.save(any(Schedule.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    // When
    Schedule editSchedule = scheduleService.editScheduleAndSave(request);

    // Then
    assertEquals(request.getTitle(), editSchedule.getTitle());
    assertEquals(request.getContent(), editSchedule.getContent());
    assertEquals(request.getStartDt(), editSchedule.getStartDt());
    assertEquals(request.getEndDt(), editSchedule.getEndDt());
    assertEquals(request.getRepeatCycle(), editSchedule.getRepeatCycle());
    assertEquals(request.isRepeat(), editSchedule.isRepeat());
    assertEquals(mockTeam, editSchedule.getTeam());
    assertEquals(scheduleCategory, editSchedule.getScheduleCategory());

    verify(scheduleRepository, times(1)).findById(scheduleId);
    verify(teamRepository, times(1)).findById(teamId);
    verify(categoryRepository, times(1)).findById(categoryId);
    verify(scheduleRepository, times(1)).save(any(Schedule.class));
  }


  @Test
  @DisplayName("일정 삭제 - 성공")
  void deleteScheduleSuccess() {
    // Given
    Long scheduleId = 1L;
    Schedule mockSchedule = Schedule.builder()
        .scheduleId(scheduleId)
        .build();

    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(mockSchedule));

    // When
    scheduleService.deleteSchedule(scheduleId);

    // Then
    verify(scheduleRepository, times(1)).delete(mockSchedule);
  }

  @Test
  @DisplayName("일정 삭제 실패 - 일정이 존재하지 않음")
  void deleteScheduleFailed() {
    // Given
    Long scheduleId = 1L;
    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

    // When, Then
    assertThrows(CustomException.class, () -> scheduleService.deleteSchedule(scheduleId));
    verify(scheduleRepository, never()).delete(any(Schedule.class));
  }
}
