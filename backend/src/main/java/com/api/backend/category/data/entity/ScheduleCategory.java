package com.api.backend.category.data.entity;

import com.api.backend.category.data.dto.ScheduleCategoryEditRequest;
import com.api.backend.category.type.CategoryType;
import com.api.backend.global.domain.BaseEntity;
import com.api.backend.schedule.data.entity.Schedule;
import com.api.backend.team.data.entity.Team;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "schedule_category")
public class ScheduleCategory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long scheduleCategoryId;
  private String color;
  private String categoryName;

  @Enumerated(EnumType.STRING)
  private CategoryType categoryType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  private Team team;

  @OneToMany(mappedBy = "scheduleCategory")
  @Builder.Default
  private List<Schedule> schedule = new ArrayList<>();

  public void editScheduleCategory(ScheduleCategoryEditRequest request) {
    if (request.getCategoryId() != null) {
      this.scheduleCategoryId = request.getCategoryId();
    }
    if (request.getCategoryName() != null) {
      this.categoryName = request.getCategoryName();
    }
    if (request.getCategoryType() != null) {
      this.categoryType = request.getCategoryType();
    }
    if (request.getColor() != null) {
      this.color = request.getColor();
    }
  }
}
