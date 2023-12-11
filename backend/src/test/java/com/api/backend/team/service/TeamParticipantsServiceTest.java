package com.api.backend.team.service;

import static com.api.backend.team.data.ResponseMessage.DELETE_TEAM_PARTICIPANT;
import static com.api.backend.team.data.ResponseMessage.UPDATE_ROLE_TEAM_PARTICIPANT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.api.backend.member.data.dto.TeamParticipantUpdateRequest;
import com.api.backend.member.data.entity.Member;
import com.api.backend.team.data.entity.Team;
import com.api.backend.team.data.entity.TeamParticipants;
import com.api.backend.team.data.repository.TeamParticipantsRepository;
import com.api.backend.team.data.type.TeamRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamParticipantsServiceTest {

  @Mock
  private TeamParticipantsRepository teamParticipantsRepository;
  @Mock
  private TeamService teamService;

  @InjectMocks
  private TeamParticipantsService teamParticipantsService;

  @Test
  @DisplayName("나 자신을 해당 팀으로 부터 탈퇴 로직")
  void deleteTeamParticipant() {
    //given
    Long userId = 1L;
    Long teamId = 1L;
    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamRole(TeamRole.MATE).build();
    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(), anyLong()
    )).thenReturn(Optional.of(teamParticipants));
    doNothing().when(teamParticipantsRepository).delete(any());
    //when
    String result = teamParticipantsService.deleteTeamParticipant(userId, teamId);

    //then
    assertEquals(result,DELETE_TEAM_PARTICIPANT);
  }

  @Test
  @DisplayName("팀장이 팀원에게 권한부여 로직")
  void updateRoleTeamParticipant(){
    //given
    Long userId = 1L;
    Long participantId = 1L;
    Long teamId = 1L;
    Team team = Team.builder().teamId(1L).build();
    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamRole(TeamRole.READER)
        .team(team)
        .build();
    TeamParticipants teamParticipants2 = TeamParticipants.builder()
        .teamRole(TeamRole.MATE)
        .team(team)
        .build();

    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(),
        anyLong()
    )).thenReturn(Optional.of(teamParticipants));
    when(teamParticipantsRepository.findById(anyLong()))
        .thenReturn(Optional.of(teamParticipants2));

    //when
    String result = teamParticipantsService.updateRoleTeamParticipant(userId, participantId,
        teamId);

    //then
    assertEquals(result,UPDATE_ROLE_TEAM_PARTICIPANT);
  }

  @Test
  @DisplayName("내가 속한 팀의 모든 팀원 조회 로직")
  void getTeamParticipants(){
    //given
    Long teamId = 1L;
    Long userId = 1L;
    List<TeamParticipants> teamParticipantsList = new ArrayList<>();

    for (int i = 0; i < 3; i++) {
      teamParticipantsList.add(
          TeamParticipants.builder()
              .teamNickName("test" + i)
              .build()
      );
    }
    Team team = Team.builder()
        .isDelete(false)
        .teamParticipants(teamParticipantsList)
        .build();
    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamNickName("test")
        .team(team).build();

    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(),anyLong()
    )).thenReturn(Optional.of(teamParticipants));
    doNothing().when(teamService).isDeletedCheck(team);

    //when
    List<TeamParticipants> result = teamParticipantsService.getTeamParticipants(teamId, userId);

    //then
    for (int i = 0; i < 3; i++) {
      assertEquals(result.get(i).getTeamNickName(),
          teamParticipantsList.get(i).getTeamNickName());
    }
  }

  @Test
  @DisplayName("내가 속한 단건 팀 참가자 조회")
  void getTeamParticipant(){
    //given
    Long teamId = 1L;
    Long userId = 1L;

    Team team = Team.builder()
        .isDelete(false)
        .build();
    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamNickName("test")
        .team(team).build();

    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(),anyLong()
    )).thenReturn(Optional.of(teamParticipants));
    doNothing().when(teamService).isDeletedCheck(team);

    //when
    TeamParticipants result = teamParticipantsService.getTeamParticipant(teamId, userId);

    //then
    assertEquals(result.getTeamNickName(),teamParticipants.getTeamNickName());
  }

  @Test
  @DisplayName("팀 참가자 수정 로직")
  void updateParticipantContent() {
    //given
    TeamParticipantUpdateRequest request = TeamParticipantUpdateRequest.builder()
        .teamParticipantsId(1L)
        .teamNickName("수정된 내용입니다.")
        .build();
    String userId = "1";

    Team team = Team.builder()
        .isDelete(false)
        .build();
    TeamParticipants teamParticipants = TeamParticipants.builder()
        .member(
            Member.builder().memberId(1L).build()
        )
        .teamNickName("test")
        .team(team).build();

    when(teamParticipantsRepository.findById(anyLong()))
        .thenReturn(Optional.of(teamParticipants));

    //when
    TeamParticipants result = teamParticipantsService.updateParticipantContent(request, userId);

    //then
    assertEquals(result.getTeamNickName(), request.getTeamNickName());
  }
}