package com.api.backend.global.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  ENUM_NOT_VALID_EXCEPTION(500, "잘못된 ENUM요청이 들어왔습니다."),
  ENUM_NOT_FOUND_EXCEPTION(500, "비어있는 ENUM입니다."),
  SCHEDULE_CATEGORY_NOT_FOUND_EXCEPTION(500, "존재하지 않는 일정 카테고리 입니다."),
  SCHEDULE_CATEGORY_ALREADY_EXIST_EXCEPTION(500, "이미 존재하는 일정 카테고리 입니다."),
  TEAM_NOT_FOUND_EXCEPTION(500, "존재하지 않는 팀입니다.");

  private final int code;
  private final String errorMessage;
}

