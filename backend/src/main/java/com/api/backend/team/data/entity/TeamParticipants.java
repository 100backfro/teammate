package com.api.backend.team.data.entity;

import com.api.backend.global.domain.BaseEntity;
import com.api.backend.member.data.entity.Member;
import com.api.backend.schedule.data.entity.TeamParticipantsSchedule;
import com.api.backend.notification.data.entity.Notification;
import com.api.backend.team.data.type.TeamRole;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "team_participants")
public class TeamParticipants extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long teamParticipantsId;

  @Enumerated(EnumType.STRING)
  private TeamRole teamRole;
  private String teamNickName;
  private String participantsProfileUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToMany(mappedBy = "teamParticipants")
  private List<Notification> notifications = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  private Team team;

  @OneToMany(mappedBy = "teamParticipants", cascade = CascadeType.ALL)
  private List<TeamParticipantsSchedule> teamParticipantsSchedules;


  public void updateRole(TeamRole teamRole) {
    this.teamRole = teamRole;
  }

  public void changeNickName(String teamNickName) {
    if (!this.teamNickName
        .equals(teamNickName)) {
      this.teamNickName = teamNickName;
    }
  }

  public void changeProfileUrl(String imgUrl) {
    participantsProfileUrl = imgUrl;
  }

}
