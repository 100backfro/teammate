package com.api.backend.member.data.entity;


import com.api.backend.global.domain.BaseEntity;
import com.api.backend.member.data.type.Authority;
import com.api.backend.member.data.type.LoginType;
import com.api.backend.member.data.type.SexType;
import com.api.backend.notification.data.entity.Notification;
import com.api.backend.team.data.entity.TeamParticipants;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;
  private String email;
  @Setter
  private String password;
  private String name;

  @Enumerated(EnumType.STRING)
  private SexType sexType;

  @Enumerated(EnumType.STRING)
  private LoginType loginType;
  private String socialId;

  @Enumerated(EnumType.STRING)
  private Authority authority;
  @Setter
  private Boolean isAuthenticatedEmail;

  // TODO: 추후 재셋팅 예정
//  @OneToMany(mappedBy = "member")
//  private List<Comment> comments = new ArrayList<>();


  @OneToMany(mappedBy = "member")
  @Builder.Default
  private List<Notification> notifications = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  @Builder.Default
  private List<TeamParticipants> teamParticipants = new ArrayList<>();
}
