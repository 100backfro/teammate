package com.api.backend.notification.controller;

import com.api.backend.notification.data.type.NotificationUserType;
import com.api.backend.notification.service.NotificationService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping(value = "/notification/team/{teamId}", produces = "text/event-stream")
  public SseEmitter subscribeTeamRequest(
      Principal principal,
      @PathVariable(value = "teamId") Long teamId
  ) {
    return notificationService.setTeamParticipantEmitter(
        teamId, Long.valueOf(principal.getName())
    );
  }
}
