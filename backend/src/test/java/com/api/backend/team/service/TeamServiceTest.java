package com.api.backend.team.service;

import static com.api.backend.global.exception.type.ErrorCode.PASSWORD_NOT_MATCH_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_CODE_NOT_VALID_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_IS_DELETEING_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_IS_DELETE_TRUE_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_NOT_DELETEING_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_EXIST_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_LEADER_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_VALID_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.api.backend.global.exception.CustomException;
import com.api.backend.member.data.entity.Member;
import com.api.backend.member.data.repository.MemberRepository;
import com.api.backend.team.data.dto.TeamDisbandRequest;
import com.api.backend.team.data.dto.TeamKickOutRequest;
import com.api.backend.team.data.dto.TeamKickOutResponse;
import com.api.backend.team.data.dto.TeamParticipantsUpdateResponse;
import com.api.backend.team.data.dto.TeamUpdateRequest;
import com.api.backend.team.data.entity.Team;
import com.api.backend.team.data.entity.TeamParticipants;
import com.api.backend.team.data.repository.TeamParticipantsRepository;
import com.api.backend.team.data.repository.TeamRepository;
import com.api.backend.team.data.type.TeamRole;
import com.api.backend.team.service.file.impl.ImgStoreImpl;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

  @Mock
  private TeamRepository teamRepository;
  @Mock
  private TeamParticipantsRepository teamParticipantsRepository;
  @Mock
  private ImgStoreImpl imgStore;
  @Mock
  private MemberRepository memberRepository;


  @InjectMocks
  private TeamService teamService;

  @Test
  void createTeam() {
    // todo img 저장 부분이 구현이 된다면 하겠습니다.
  }

  @Test
  @DisplayName("팀 초대 코드 url 로직 - [성공]")
  void getTeamUrl_success() {
    //given
    Long id = 1L;
    Long userId = 1L;
    Team team = Team.builder()
        .teamId(1L)
        .inviteLink("2/dsfefsefnklsd")
        .build();
    when(teamRepository.findById(anyLong()))
        .thenReturn(Optional.of(team));
    when(teamParticipantsRepository.existsByTeam_TeamIdAndMember_MemberId(id, Long.valueOf(userId)))
        .thenReturn(true);
    //when
    String result = teamService.getTeamUrl(id,userId);

    //then
    assertEquals(result,team.getInviteLink());
  }

  @Test
  @DisplayName("팀 초대 코드 url 로직 - [실패] 팀원이 아닐경우")
  void getTeamUrl_fail() {
    //given
    Long id = 1L;
    Long userId = 1L;
    Team team = Team.builder()
        .teamId(1L)
        .profileUrl("2/dsfefsefnklsd")
        .build();
    when(teamRepository.findById(anyLong()))
        .thenReturn(Optional.of(team));
    when(teamParticipantsRepository.existsByTeam_TeamIdAndMember_MemberId(id, Long.valueOf(userId)))
        .thenReturn(false);
    //when
    CustomException result = assertThrows(CustomException.class,
        () -> teamService.getTeamUrl(id,userId));

    //then
    assertEquals(result.getErrorMessage(),TEAM_PARTICIPANTS_NOT_VALID_EXCEPTION.getErrorMessage());
    assertEquals(result.getErrorCode().getCode(),500);
  }

  @Test
  @DisplayName("팀에 팀원 추가 로직 - 성공")
  void updateTeamParticipants_success(){
    //given
    Long id = 1L;
    Long userId = 1L;
    String code = "dsfefsefnklsd";
    Team team = Team.builder()
        .teamId(1L)
        .name("test")
        .inviteLink("2/dsfefsefnklsd")
        .build();
    when(teamRepository.findById(anyLong()))
        .thenReturn(Optional.of(team));
    when(teamParticipantsRepository.existsByTeam_TeamIdAndMember_MemberId(
        anyLong(), anyLong()
    )).thenReturn(false);
    when(memberRepository.findById(anyLong()))
        .thenReturn(Optional.of(Member.builder().build()));
    //when
    TeamParticipantsUpdateResponse result = teamService.updateTeamParticipants(id, code, userId);

    //then
    assertEquals(result.getTeamId(), team.getTeamId());
    assertEquals(result.getTeamName(), team.getName());
  }

  @Test
  @DisplayName("팀에 팀원 추가 로직 - 실패[code]")
  void updateTeamParticipants_fail_code(){
    //given
    Long id = 1L;
    Long userId = 1L;
    String code = "sadsadasd";
    Team team = Team.builder()
        .teamId(1L)
        .inviteLink("2/dsfefsefnklsd")
        .build();
    when(teamRepository.findById(anyLong()))
        .thenReturn(Optional.of(team));

    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.updateTeamParticipants(id, code, userId)
    );

    //then
    assertEquals(result.getErrorMessage(),TEAM_CODE_NOT_VALID_EXCEPTION.getErrorMessage());
    assertEquals(result.getErrorCode().getCode(),TEAM_CODE_NOT_VALID_EXCEPTION.getCode());
  }

  @Test
  @DisplayName("팀에 팀원 추가 로직 - 실패[existUser]")
  void updateTeamParticipants_fail_exist_user(){
    //given
    Long id = 1L;
    Long userId = 1L;
    String code = "dsfefsefnklsd";
    Team team = Team.builder()
        .teamId(1L)
        .inviteLink("2/dsfefsefnklsd")
        .build();
    when(teamRepository.findById(anyLong()))
        .thenReturn(Optional.of(team));
    when(teamParticipantsRepository.existsByTeam_TeamIdAndMember_MemberId(anyLong(),anyLong()))
        .thenReturn(true);

    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.updateTeamParticipants(id, code, userId)
    );

    //then
    assertEquals(result.getErrorMessage(),TEAM_PARTICIPANTS_EXIST_EXCEPTION.getErrorMessage());
    assertEquals(result.getErrorCode().getCode(),TEAM_PARTICIPANTS_EXIST_EXCEPTION.getCode());
  }

  @Test
  @DisplayName("팀원 강퇴 로직 - 성공")
  void KickOutTeamParticipants_success(){
    //given
    Long userId = 1L;
    TeamKickOutRequest request =
        new TeamKickOutRequest(1L, 1L, "testMessage");
    TeamParticipants teamParticipants = TeamParticipants
        .builder()
        .teamParticipantsId(1L)
        .teamRole(TeamRole.READER)
        .member(
            Member.builder().memberId(1L).build()
        ).build();
    TeamParticipants teamParticipants2 = TeamParticipants
        .builder()
        .teamParticipantsId(2L)
        .teamRole(TeamRole.READER)
        .member(
            Member.builder().memberId(1L).name("testUser").build()
        ).build();

    when(teamRepository.existsByTeamIdAndIsDelete(anyLong(), anyBoolean()))
        .thenReturn(true);
    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(anyLong(), anyLong()))
        .thenReturn(Optional.of(teamParticipants2))
        .thenReturn(Optional.of(teamParticipants));
    doNothing().when(teamParticipantsRepository).delete(any());
    //when
    TeamKickOutResponse result = teamService.kickOutTeamParticipants(request, userId);

    //then
    assertEquals(result.getKickOutMemberId(),1L);
  }
  @Test
  @DisplayName("팀원 강퇴 로직 - 실패")
  void KickOutTeamParticipants_fail(){
    //given
    TeamKickOutRequest request =
        new TeamKickOutRequest(1L, 1L, "testMessage");
    Long userId = 1L;

    when(teamRepository.existsByTeamIdAndIsDelete(anyLong(), anyBoolean()))
        .thenReturn(true);
    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(anyLong(), anyLong()))
        .thenReturn(Optional.empty());
    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.kickOutTeamParticipants(request, userId)
    );

    //then
    assertEquals(result.getErrorCode().getCode(),
        TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION.getCode());
    assertEquals(result.getErrorCode().getErrorMessage(),
        TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION.getErrorMessage());
  }


  @Test
  @DisplayName("팀 해체 로직 - 성공")
  void disbandTeam_success() {
    //given
    Long userId = 1L;
    TeamDisbandRequest teamDisbandRequest = new TeamDisbandRequest(1L, "test");
    Member member = Member.builder().memberId(1L).password("test").build();
    Team team = Team.builder()
        .isDelete(false)
        .build();
    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamRole(TeamRole.READER)
        .member(member)
        .team(team)
        .build();

    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(), any()
    )).thenReturn(Optional.of(teamParticipants));

    //when
    Team result = teamService.disbandTeam(userId, teamDisbandRequest);

    //then
    assertEquals(result.getRestorationDt(), LocalDate.now().plusDays(30));
  }

  @Test
  @DisplayName("팀 해체 로직 - 실패[팀원이 존재하지 않음]")
  void disbandTeam_fail_participants() {
    //given
    Long userId = 1L;
    TeamDisbandRequest teamDisbandRequest = new TeamDisbandRequest(1L, "test");

    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(), any()
    )).thenReturn(Optional.empty());

    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.disbandTeam(userId, teamDisbandRequest)
    );

    //then
    assertEquals(result.getErrorCode().getErrorMessage(), TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION.getErrorMessage());
    assertEquals(result.getErrorCode().getCode(), TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION.getCode());
  }

  @Test
  @DisplayName("팀 해체 로직 - 실패[권한 부족]")
  void disbandTeam_fail_mate() {
    //given
    Long userId = 1L;
    TeamDisbandRequest teamDisbandRequest = new TeamDisbandRequest(1L, "test");
    Member member = Member.builder().memberId(1L).password("test").build();
    Team team = Team.builder()
        .isDelete(false)
        .build();
    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamRole(TeamRole.MATE)
        .member(member)
        .team(team)
        .build();
    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(), any()
    )).thenReturn(Optional.of(teamParticipants));

    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.disbandTeam(userId, teamDisbandRequest)
    );

    //then
    assertEquals(result.getErrorCode().getErrorMessage(), TEAM_PARTICIPANTS_NOT_LEADER_EXCEPTION.getErrorMessage());
    assertEquals(result.getErrorCode().getCode(), TEAM_PARTICIPANTS_NOT_LEADER_EXCEPTION.getCode());
  }

  @Test
  @DisplayName("팀 해체 로직 - 실패[비밀번호 불일치]")
  void disbandTeam_fail_password() {
    //given
    Long userId = 1L;
    TeamDisbandRequest teamDisbandRequest = new TeamDisbandRequest(1L, "test");
    Member member = Member.builder().memberId(1L).password("asdasdswd").build();
    Team team = Team.builder()
        .isDelete(false)
        .build();
    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamRole(TeamRole.READER)
        .member(member)
        .team(team)
        .build();
    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(), any()
    )).thenReturn(Optional.of(teamParticipants));

    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.disbandTeam(userId, teamDisbandRequest)
    );

    //then
    assertEquals(result.getErrorCode().getErrorMessage(), PASSWORD_NOT_MATCH_EXCEPTION.getErrorMessage());
    assertEquals(result.getErrorCode().getCode(), PASSWORD_NOT_MATCH_EXCEPTION.getCode());
  }

  @Test
  @DisplayName("팀 해체 로직 - 실패[복구 시간 not null]")
  void disbandTeam_fail_restoreDt() {
    //given
    Long userId = 1L;
    TeamDisbandRequest teamDisbandRequest = new TeamDisbandRequest(1L, "test");
    Member member = Member.builder().memberId(1L).password("test").build();
    Team team = Team.builder()
        .isDelete(false)
        .restorationDt(LocalDate.now())
        .build();

    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamRole(TeamRole.READER)
        .member(member)
        .team(team)
        .build();
    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(), any()
    )).thenReturn(Optional.of(teamParticipants));

    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.disbandTeam(userId, teamDisbandRequest)
    );

    //then
    assertEquals(result.getErrorCode().getErrorMessage(), TEAM_IS_DELETEING_EXCEPTION.getErrorMessage());
    assertEquals(result.getErrorCode().getCode(), TEAM_IS_DELETEING_EXCEPTION.getCode());
  }

  @Test
  @DisplayName("팀 해체 로직 - 실패[팀이 이미 해체됨]")
  void disbandTeam_fail_delete() {
    //given
    Long userId = 1L;
    TeamDisbandRequest teamDisbandRequest = new TeamDisbandRequest(1L, "test");
    Member member = Member.builder().memberId(1L).password("test").build();
    Team team = Team.builder()
        .isDelete(true)
        .build();

    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamRole(TeamRole.READER)
        .member(member)
        .team(team)
        .build();
    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(), any()
    )).thenReturn(Optional.of(teamParticipants));

    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.disbandTeam(userId, teamDisbandRequest)
    );

    //then
    assertEquals(result.getErrorCode().getErrorMessage(), TEAM_IS_DELETE_TRUE_EXCEPTION.getErrorMessage());
    assertEquals(result.getErrorCode().getCode(), TEAM_IS_DELETE_TRUE_EXCEPTION.getCode());
  }


  @Test
  @DisplayName("팀 복구 로직 - 성공[복구 성공]")
  void restoreTeam_success_restore_success(){
    //given
    Long userId = 1L;
    LocalDate restoreDt = LocalDate.now().minusDays(19L);
    Long teamId = 1L;

    Member member = Member.builder().memberId(1L).password("test").build();
    Team team = Team.builder()
        .isDelete(false)
        .restorationDt(LocalDate.now())
        .build();

    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamRole(TeamRole.READER)
        .member(member)
        .team(team)
        .build();
    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(), any()
    )).thenReturn(Optional.of(teamParticipants));

    //when
    Team result = teamService.restoreTeam(userId, restoreDt, teamId);

    //then
    assertEquals(result.getTeamId(),team.getTeamId());
    assertFalse(result.isDelete());
  }

  @Test
  @DisplayName("팀 복구 로직 - 실패[권한 부족]")
  void restoreTeam_fail_mate(){
    //given
    Long userId = 1L;
    LocalDate restoreDt = LocalDate.now().minusDays(19L);
    Long teamId = 1L;

    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.restoreTeam(userId, restoreDt, teamId)
    );

    //then
    assertEquals(result.getErrorCode().getCode(),TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION.getCode());
    assertEquals(result.getErrorCode().getErrorMessage(),TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION.getErrorMessage());
  }

  @Test
  @DisplayName("팀 복구 로직 - 실패[이미 해체된 팀]")
  void restoreTeam_fail_isDelete(){
    //given
    Long userId = 1L;
    LocalDate restoreDt = LocalDate.now().minusDays(19L);
    Long teamId = 1L;
    Member member = Member.builder().memberId(1L).password("test").build();
    Team team = Team.builder()
        .isDelete(true)
        .restorationDt(LocalDate.now())
        .build();

    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamRole(TeamRole.READER)
        .member(member)
        .team(team)
        .build();

    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(), any()
    )).thenReturn(Optional.of(teamParticipants));

    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.restoreTeam(userId, restoreDt, teamId)
    );

    //then
    assertEquals(result.getErrorCode().getCode(),TEAM_IS_DELETE_TRUE_EXCEPTION.getCode());
    assertEquals(result.getErrorCode().getErrorMessage(),TEAM_IS_DELETE_TRUE_EXCEPTION.getErrorMessage());
  }

  @Test
  @DisplayName("팀 복구 로직 - 실패[restoreDt null]")
  void restoreTeam_fail_restoreDt(){
    //given
    Long userId = 1L;
    LocalDate restoreDt = LocalDate.now().minusDays(19L);
    Long teamId = 1L;
    Member member = Member.builder().memberId(1L).password("test").build();
    Team team = Team.builder()
        .isDelete(false)
        .build();

    TeamParticipants teamParticipants = TeamParticipants.builder()
        .teamRole(TeamRole.READER)
        .member(member)
        .team(team)
        .build();

    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(
        anyLong(), any()
    )).thenReturn(Optional.of(teamParticipants));

    //when
    CustomException result = assertThrows(
        CustomException.class,
        () -> teamService.restoreTeam(userId, restoreDt, teamId)
    );

    //then
    assertEquals(result.getErrorCode().getCode(),TEAM_NOT_DELETEING_EXCEPTION.getCode());
    assertEquals(result.getErrorCode().getErrorMessage(),TEAM_NOT_DELETEING_EXCEPTION.getErrorMessage());
  }

  @Test
  @DisplayName("자신이 속한 팀 조회시 팀 해체여부가 false 인 것만 받아온다.")
  void getTimes(){
    //given
    Long userId = 1L;
    Pageable pageable = PageRequest.of(0,10);
    List<Team> teams = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      teams.add(
          Team.builder()
              .teamId((long) i)
              .build()
      );
    }
    Page<Team> teamPage = new PageImpl<>(teams);
    when(teamRepository.findAllByTeamParticipants_Member_MemberIdAndIsDelete(
        anyLong(), anyBoolean(), any()
    )).thenReturn(teamPage);

    //when
    Page<Team> result = teamService.getTeams(userId, pageable);

    //then
    List<Team> resultTeam = result.getContent();
    for (int i = 0; i < 3; i++) {
      assertEquals(resultTeam.get(i).getTeamId(),i);
    }
  }

  @Test
  @DisplayName("팀 내용수정 service로직")
  void updateTeam(){
    //given
    MultipartFile multipartFile = new MockMultipartFile("Img", new byte[2]);
    TeamUpdateRequest teamUpdateRequest =
        new TeamUpdateRequest(1L, "test", multipartFile);
    Long userId = 1L;
    Team team = Team.builder()
        .name("test1")
        .profileUrl("test").build();
    String testUrl = "teamUrl";
    TeamParticipants teamParticipants = TeamParticipants.builder()
        .team(team)
        .teamRole(TeamRole.READER)
        .build();
    when(teamParticipantsRepository.findByTeam_TeamIdAndMember_MemberId(anyLong(), anyLong()))
        .thenReturn(Optional.of(teamParticipants));
    when(imgStore.uploadImg(any(), any(), any()))
        .thenReturn(testUrl);
    //when
    Team result = teamService.updateTeam(teamUpdateRequest, userId);

    //then
    assertEquals(result.getName(),teamUpdateRequest.getTeamName());
    assertEquals(result.getProfileUrl() , testUrl);
  }
}