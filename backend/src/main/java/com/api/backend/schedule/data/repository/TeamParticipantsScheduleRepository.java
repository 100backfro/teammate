package com.api.backend.schedule.data.repository;

import com.api.backend.schedule.data.entity.TeamParticipantsSchedule;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamParticipantsScheduleRepository extends JpaRepository<TeamParticipantsSchedule, Long> {
  List<TeamParticipantsSchedule> findAllBySchedule_ScheduleId (Long scheduleId);
}
