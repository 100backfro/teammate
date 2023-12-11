package com.api.backend.category.service;

import static com.api.backend.global.exception.type.ErrorCode.SCHEDULE_CATEGORY_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_NOT_FOUND_EXCEPTION;

import com.api.backend.category.data.dto.ScheduleCategoryEditRequest;
import com.api.backend.category.data.dto.ScheduleCategoryRequest;
import com.api.backend.category.data.entity.ScheduleCategory;
import com.api.backend.category.data.repository.ScheduleCategoryRepository;
import com.api.backend.category.type.CategoryType;
import com.api.backend.global.exception.CustomException;
import com.api.backend.team.data.entity.Team;
import com.api.backend.team.data.repository.TeamRepository;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ScheduleCategoryService {

  private final ScheduleCategoryRepository scheduleCategoryRepository;
  private final TeamRepository teamRepository;


  @Transactional
  public ScheduleCategory add(ScheduleCategoryRequest scheduleCategoryRequest) {
    Team team = validateTeam(scheduleCategoryRequest.getTeamId());

    ScheduleCategory scheduleCategory = ScheduleCategory.builder()
        .team(team)
        .categoryName(scheduleCategoryRequest.getCategoryName())
        .categoryType(scheduleCategoryRequest.getCategoryType())
        .color(scheduleCategoryRequest.getColor())
        .build();
    scheduleCategoryRepository.save(scheduleCategory);
    return scheduleCategory;
  }


  public Page<ScheduleCategory> searchByCategoryType(CategoryType categoryType,
      Pageable pageable, Long teamId) {
    return scheduleCategoryRepository.findAllByCategoryTypeAndTeam_TeamId(categoryType, pageable, teamId);
  }

  @Transactional
  public ScheduleCategory edit(ScheduleCategoryEditRequest scheduleCategoryEditRequest) {
    validateTeam(scheduleCategoryEditRequest.getTeamId());
    ScheduleCategory scheduleCategory = validateScheduleCategory(
        scheduleCategoryEditRequest.getCategoryId());
    scheduleCategory.editScheduleCategory(scheduleCategoryEditRequest);
    return scheduleCategoryRepository.save(scheduleCategory);
  }

  @Transactional
  public void delete(Long categoryId) {
    validateScheduleCategory(categoryId);
    scheduleCategoryRepository.deleteById(categoryId);
  }


  public Team validateTeam(Long teamId) {
    return teamRepository.findById(teamId)
        .orElseThrow(() -> new CustomException(TEAM_NOT_FOUND_EXCEPTION));
  }

  public ScheduleCategory validateScheduleCategory(Long scheduleCategoryId) {
    return scheduleCategoryRepository.findById(scheduleCategoryId)
        .orElseThrow(() -> new CustomException(SCHEDULE_CATEGORY_NOT_FOUND_EXCEPTION));
  }

}
