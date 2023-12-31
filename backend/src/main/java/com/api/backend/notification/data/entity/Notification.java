package com.api.backend.notification.data.entity;

import com.api.backend.global.domain.BaseEntity;
import com.api.backend.member.data.entity.Member;
import com.api.backend.notification.data.type.AlarmType;
import com.api.backend.team.data.entity.TeamParticipants;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "notification")
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long notificationId;

  @Enumerated(EnumType.STRING)
  private AlarmType alarmType;

  private String nickName;
  private String message;
  @Column(columnDefinition = "boolean default false")
  @Setter
  private boolean isRead;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_participants_id")
  private TeamParticipants teamParticipants;

  public static Notification convertToMemberNotify(Long memberId, String message, AlarmType alarmType) {
    return Notification.builder()
        .member(Member.builder().memberId(memberId).build())
        .message(message)
        .alarmType(alarmType)
        .build();
  }

  public static Notification convertToMemberNotify(Member member, String message, AlarmType alarmType) {
    return Notification.builder()
        .member(member)
        .message(message)
        .alarmType(alarmType)
        .build();
  }

  public static Notification convertNickNameToTeamParticipantsNotify(
      TeamParticipants teamParticipants,
      String updateParticipantNickName,
      String message,
      AlarmType alarmType
  ) {
    return Notification.builder()
        .teamParticipants(teamParticipants)
        .nickName(updateParticipantNickName)
        .message(message)
        .alarmType(alarmType)
        .build();
  }
}
