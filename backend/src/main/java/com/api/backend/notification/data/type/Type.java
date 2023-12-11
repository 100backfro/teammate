package com.api.backend.notification.data.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {
  DOCUMENTS("문서"),
  COMMENT("댓글"),
  KICKOUT("강퇴"),
  MENTION("멘션"),
  INVITE("초대");

  private final String description;

}
