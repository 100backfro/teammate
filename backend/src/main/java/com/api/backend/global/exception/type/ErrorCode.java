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
  SCHEDULE_NOT_FOUND_EXCEPTION(500, "존재하지 않는 일정입니다."),
  INVALID_REPEAT_CYCLE_EXCEPTION(400, "잘못된 반복 주기입니다."),

  // team
  TEAM_NOT_FOUND_EXCEPTION(500, "존재하지 않는 팀입니다."),
  TEAM_CODE_NOT_VALID_EXCEPTION(500, "팀 코드가 일치하지 않습니다."),
  TEAM_NOT_EQUALS_EXCEPTION(500, "팀이 일치하지 않습니다."),
  TEAM_IS_DELETE_TRUE_EXCEPTION(500, "해당 팀 이미 해체된 팀입니다."),
  TEAM_IS_DELETEING_EXCEPTION(500, "해당 팀 이미 해체 중에 있습니다."),
  TEAM_NOT_DELETEING_EXCEPTION(500, "해당 팀은 해체 중이 아닙니다."),
  TEAM_RESTORE_EXPIRED_EXCEPTION(500, "해당 팀은 해체 기한이 지났습니다."),

  // teamParticipant
  TEAM_PARTICIPANT_DELETE_NOT_VALID_EXCEPTION(500, "팀장 권한을 위임해야지 팀 탈퇴가 가능합니다."),
  TEAM_PARTICIPANT_NOT_VALID_READER_EXCEPTION(500, "팀장 권한이 있어야 합니다."),
  TEAM_PARTICIPANT_EQUALS_EXCEPTION(500, "동일한 회원을 삭제할려고 하고 있습니다."),
  TEAM_PARTICIPANT_NOT_VALID_MATE_EXCEPTION(500, "팀원 권한이 있어야 합니다."),
  TEAM_PARTICIPANTS_EXIST_EXCEPTION(500, "이미 해당 팀의 일원입니다."),
  TEAM_PARTICIPANTS_EQUALS_EXCEPTION(500, "나 자신을 가리키고 있습니다.."),
  TEAM_PARTICIPANTS_NOT_LEADER_EXCEPTION(500, "팀 해체는 리더만 가능합니다."),
  TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION(500, "존재하지 않는 팀원입니다."),
  TEAM_PARTICIPANTS_NOT_VALID_EXCEPTION(500, "해당 팀의 팀원이 아닙니다."),

  MEMBER_NOT_FOUND_EXCEPTION(500, "존재하지 않는 회원입니다."),
  MEMBER_NOT_EQUALS_EXCEPTION(500, "회원 정보가 일치하지 않습니다."),
  TOKEN_EXPIRED_EXCEPTION(500, "토큰이 만료 되었습니다."),
  EMAIL_NOT_FOUND_EXCEPTION(400, "해당 이메일로 가입한 사용자가 존재하지 않습니다."),
  EMAIL_ALREADY_EXIST_EXCEPTION(409,"해당 이메일로 가입한 사용자가 존재합니다."),
  PASSWORD_NOT_MATCH_EXCEPTION(400, "비밀번호가 일치하지 않습니다."),
  TOKEN_NOT_FOUND_PERMISSION_INFORMATION(400,"권한 정보가 없는 토큰입니다."),
  TOKEN_INVALID_EXCEPTION(400,"유효하지 않는 토큰입니다."),

  // principal
  PRINCIPAL_IS_NULL(400, "인증이 되지 않았습니다."),

  // documents
  DOCUMENT_NOT_IN_TEAM_EXCEPTION(400, "해당 팀에 속하지 않은 문서입니다."),
  DOCUMENT_WRITER_UNMATCH_TEAM_PARTICIPANTS_EXCEPTION(400, "해당 문서의 생성자가 아닙니다."),
  DOCUMENT_NOT_FOUND_EXCEPTION(400, "존재하지 않는 문서입니다."),

  // comment
  COMMENT_NOT_FOUND_EXCEPTION(400, "존재하지 않는 댓글 입니다."),
  COMMENT_UNMATCH_WRITER_ID_EXCEPTION(400, "댓글의 작성자가 아닙니다.");

  private final int code;
  private final String errorMessage;
}

