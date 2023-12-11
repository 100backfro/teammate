package com.api.backend.member.data.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TeamParticipantUpdateRequest {

  @NotNull(message = "팀 참가자 ID가 비어있습니다.")
  private Long teamParticipantsId;
  @NotNull(message = "팀 닉네임이 null 입니다.")
  private String teamNickName;
  private MultipartFile participantImg;
}
