package com.api.backend.schedule.data.repository;

import com.api.backend.category.type.CategoryType;
import com.api.backend.schedule.data.entity.Schedule;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

  Schedule findScheduleByScheduleIdAndTeam_TeamId(Long scheduleId, Long teamId);

  Page<Schedule> findByTeam_TeamIdAndStartDtBetweenAndScheduleCategory_CategoryType(Long teamId, LocalDateTime startDt,
      LocalDateTime endDt, CategoryType type, Pageable pageable);


  Page<Schedule> findByTeam_TeamIdAndStartDtBetween(Long teamId, LocalDateTime startDt,
      LocalDateTime endDt, Pageable pageable);
}
