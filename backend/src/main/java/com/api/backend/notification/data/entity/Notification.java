package com.api.backend.notification.data.entity;

import com.api.backend.global.domain.BaseEntity;
import com.api.backend.member.data.entity.Member;
import com.api.backend.notification.data.type.Type;
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
  private Type type;
  private String teamName;
  private String message;
  private String targetUrl;
  @Column(columnDefinition = "boolean default false")
  private boolean isRead;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_participants_id")
  private TeamParticipants teamParticipants;
}
