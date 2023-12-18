package com.api.backend.schedule.data.dto;

import com.api.backend.schedule.customValidAnnotation.StartAndEndDtCheck;
import com.api.backend.schedule.data.type.RepeatCycle;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@StartAndEndDtCheck(scheduleStart = "startDt", scheduleEnd = "endDt")
//단순일정  -> 단순일정
public class SimpleScheduleInfoEditRequest {
  private Long simpleScheduleId;
  private Long teamId;
  private Long categoryId;
  private String title;
  private String content;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime startDt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime endDt;
  private String place;
  private RepeatCycle repeatCycle;
  private List<Long> teamParticipantsIds;
  private String color;
}