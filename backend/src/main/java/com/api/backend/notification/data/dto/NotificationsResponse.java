package com.api.backend.notification.data.dto;

import com.api.backend.notification.data.entity.Notification;
import com.api.backend.notification.data.type.AlarmType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsResponse {

  private Long notificationId;
  private AlarmType alarmType;
  private String nickName;
  private String message;
  private LocalDateTime createDt;

  public static NotificationsResponse from(Notification notification) {
    return NotificationsResponse.builder()
        .notificationId(notification.getNotificationId())
        .message(notification.getMessage())
        .alarmType(notification.getAlarmType())
        .createDt(notification.getCreateDt())
        .nickName(notification.getNickName())
        .build();
  }

  public static Page<NotificationsResponse> fromDtos(Page<Notification> notificationsPage){
    return notificationsPage.map(NotificationsResponse::from);
  }
}
