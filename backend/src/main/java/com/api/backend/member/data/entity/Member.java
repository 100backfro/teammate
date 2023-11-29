package com.api.backend.member.data.entity;

import com.api.backend.comment.data.entity.Comment;
import com.api.backend.global.domain.BaseEntity;
import com.api.backend.member.data.type.Authority;
import com.api.backend.member.data.type.LoginType;
import com.api.backend.member.data.type.SexType;
import com.api.backend.member.data.type.converter.AuthorityConverter;
import com.api.backend.member.data.type.converter.LoginTypeConverter;
import com.api.backend.notification.data.entity.Notification;
import com.api.backend.team.data.entity.TeamParticipants;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;
  private String email;
  private String password;
  private String name;
  private String nickName;
  private SexType sexType;
  @Convert(converter = LoginTypeConverter.class)
  private LoginType loginType;
  @Convert(converter = AuthorityConverter.class)
  private Authority authority;
  private String memberProfileUrl;

  @OneToMany(mappedBy = "member")
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private List<Notification> notifications = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  private List<TeamParticipants> teamParticipants = new ArrayList<>();
}
