package com.api.backend.schedule.data.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RepeatCycle {
  WEEKLY("매주"),
  MONTHLY("매달"),
  YEARLY("매년");
  private final String description;
}
