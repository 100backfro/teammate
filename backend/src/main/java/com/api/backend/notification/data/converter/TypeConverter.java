package com.api.backend.notification.data.converter;

import static com.api.backend.notification.data.NotificationMessage.KICK_OUT_TEAM;
import static com.api.backend.notification.data.NotificationMessage.UPDATE_TEAM_PARTICIPANT_TEAM;

import com.api.backend.member.data.entity.Member;
import com.api.backend.notification.data.entity.Notification;
import com.api.backend.notification.data.type.Type;
import com.api.backend.notification.service.NotificationService;
import com.api.backend.team.data.dto.TeamKickOutResponse;
import com.api.backend.team.data.dto.TeamParticipantsUpdateResponse;
import com.api.backend.team.data.entity.TeamParticipants;
import com.api.backend.team.service.TeamParticipantsService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class TypeConverter {

  private final NotificationService notificationService;
  private final TeamParticipantsService teamParticipantsService;

  @Async
  public void convertToDtoAndSendOrThrowsNotFoundClass(Object source) {

    if (source instanceof TeamKickOutResponse) {
      Notification result = convertToNotifyByKickOutDto((TeamKickOutResponse) source);
      notificationService.saveNotification(result);
    } else if (source instanceof TeamParticipantsUpdateResponse) {
      TeamParticipantsUpdateResponse response = (TeamParticipantsUpdateResponse) source;

      List<TeamParticipants> teamParticipants = teamParticipantsService.getTeamParticipantsExcludeId(
          response.getUpdateTeamParticipantId(), response.getTeamId()
      );

      List<Notification> notifications = teamParticipants
          .stream().map(this::convertToNotifyByNotification)
          .collect(Collectors.toList());

      notificationService.saveAllNotification(notifications);

      List<SseEmitter> sseEmitters = notificationService.getEmitters(
          response.getTeamId(),
          response.getUpdateTeamParticipantId()
      );

      List<String> emitterIds = teamParticipants.stream()
          .map(i -> NotificationService
              .createEmitterIdByTeamIdAndTeamParticipantId(response.getTeamId(),i.getTeamParticipantsId())
          )
          .collect(Collectors.toList());

    }
  }

  private Notification convertToNotifyByKickOutDto(TeamKickOutResponse teamKickOutResponse) {
    return Notification.builder()
        .member(
            Member.builder()
                .memberId(teamKickOutResponse.getKickOutMemberId())
                .build()
        )
        .message(KICK_OUT_TEAM)
        .type(Type.KICKOUT)
        .build();
  }

  private Notification convertToNotifyByNotification(TeamParticipants teamParticipants) {
    return Notification.builder()
        .teamParticipants(
            TeamParticipants.builder()
                .teamParticipantsId(teamParticipants.getTeamParticipantsId())
                .build()
        )
        .message(UPDATE_TEAM_PARTICIPANT_TEAM)
        .type(Type.INVITE)
        .build();
  }
}