package com.api.backend.team.service;

import static com.api.backend.global.exception.type.ErrorCode.MEMBER_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.PASSWORD_NOT_MATCH_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_CODE_NOT_VALID_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_IS_DELETEING_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_IS_DELETE_TRUE_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_NOT_DELETEING_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_EQUALS_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_EXIST_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_LEADER_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_VALID_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANT_NOT_VALID_READER_EXCEPTION;
import static com.api.backend.team.data.ResponseMessage.KICK_OUT_TEAM_PARTICIPANTS;
import static com.api.backend.team.data.ResponseMessage.UPDATE_TEAM_PARTICIPANTS;

import com.api.backend.global.exception.CustomException;
import com.api.backend.member.data.entity.Member;
import com.api.backend.member.data.repository.MemberRepository;
import com.api.backend.team.data.dto.TeamCreateRequest;
import com.api.backend.team.data.dto.TeamCreateResponse;
import com.api.backend.team.data.dto.TeamDisbandRequest;
import com.api.backend.team.data.dto.TeamKickOutRequest;
import com.api.backend.team.data.dto.TeamKickOutResponse;
import com.api.backend.team.data.dto.TeamParticipantsDto;
import com.api.backend.team.data.dto.TeamParticipantsUpdateResponse;
import com.api.backend.team.data.dto.TeamUpdateRequest;
import com.api.backend.team.data.entity.Team;
import com.api.backend.team.data.entity.TeamParticipants;
import com.api.backend.team.data.repository.TeamParticipantsRepository;
import com.api.backend.team.data.repository.TeamRepository;
import com.api.backend.team.data.type.ImgType;
import com.api.backend.team.data.type.TeamRole;
import com.api.backend.team.service.file.impl.ImgStoreImpl;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {

  private final TeamRepository teamRepository;
  private final MemberRepository memberRepository;
  private final TeamParticipantsRepository teamParticipantsRepository;
  private final boolean DELETE_FALSE_FLAG = false;
  private final ImgStoreImpl imgStore;
  @Transactional
  public TeamCreateResponse createTeam(TeamCreateRequest teamRequest, Long userId) {
    Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND_EXCEPTION));

    String imgUrl = imgStore.uploadImg(teamRequest.getTeamImg(), ImgType.TEAM, teamRequest.getTeamName());

    Team team = teamRepository.save(
        Team.builder()
            .memberLimit(teamRequest.getMemberLimit())
            .name(teamRequest.getTeamName())
            .profileUrl(imgUrl)
            .build()
    );
    team.setInviteLink();

    teamParticipantsRepository.save(
        TeamParticipants.builder()
            .team(team)
            .member(member)
            .teamNickName(getRandomNickName(member.getName()))
            .teamRole(TeamRole.READER)
            .build()
    );

    return TeamCreateResponse.from(team,userId);
  }

  private String getRandomNickName(String name){
    return RandomStringUtils.randomAlphanumeric(4) + "_" + name;
  }

  private Team getTeam(Long id) {
    return teamRepository.findById(id)
        .orElseThrow(() -> new CustomException(TEAM_NOT_FOUND_EXCEPTION));
  }

  public String getTeamUrl(Long teamId,Long userId) {
    Team team = getTeam(teamId);

    if (team.isDelete()) {
      throw new CustomException(TEAM_PARTICIPANTS_EXIST_EXCEPTION);
    }

    if (!teamParticipantsRepository.existsByTeam_TeamIdAndMember_MemberId(teamId, userId)) {
      throw new CustomException(TEAM_PARTICIPANTS_NOT_VALID_EXCEPTION);
    }
    return team.getInviteLink();
  }

  @Transactional
  public TeamParticipantsUpdateResponse updateTeamParticipants(Long teamId, String code, Long userId) {
    Team team = getTeam(teamId);
    String entityCode = team.getInviteLink().split("/")[1];

    if (!entityCode.equals(code)) {
      throw new CustomException(TEAM_CODE_NOT_VALID_EXCEPTION);
    }

    if (team.isDelete()) {
      throw new CustomException(TEAM_PARTICIPANTS_EXIST_EXCEPTION);
    }

    if (teamParticipantsRepository.existsByTeam_TeamIdAndMember_MemberId(teamId, userId)) {
      throw new CustomException(TEAM_PARTICIPANTS_EXIST_EXCEPTION);
    }

    Member member = memberRepository.findById(userId)
        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND_EXCEPTION));

    TeamParticipants teamParticipants = teamParticipantsRepository.save(
        TeamParticipants.builder()
            .member(member)
            .teamNickName(getRandomNickName(member.getName()))
            .team(team)
            .teamRole(TeamRole.MATE)
            .build()
    );

    return TeamParticipantsUpdateResponse
        .builder().teamName(team.getName())
        .teamId(teamId)
        .updateTeamParticipantId(teamParticipants.getTeamParticipantsId())
        .updateTeamParticipantNickName(teamParticipants.getTeamNickName())
        .message(team.getName() + UPDATE_TEAM_PARTICIPANTS)
        .build();
  }

  @Transactional
  public TeamKickOutResponse kickOutTeamParticipants(TeamKickOutRequest request, Long userId) {
    if (!teamRepository.existsByTeamIdAndIsDelete(request.getTeamId(), DELETE_FALSE_FLAG)) {
      throw new CustomException(TEAM_IS_DELETE_TRUE_EXCEPTION);
    }

    TeamParticipants leaderParticipants = teamParticipantsRepository
        .findByTeam_TeamIdAndMember_MemberId(request.getTeamId(), userId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    if (!leaderParticipants.getTeamRole().equals(TeamRole.READER)) {
      throw new CustomException(TEAM_PARTICIPANT_NOT_VALID_READER_EXCEPTION);
    }

    TeamParticipants teamParticipants = teamParticipantsRepository
        .findByTeam_TeamIdAndMember_MemberId(request.getTeamId(), request.getUserId())
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    if (teamParticipants.getTeamParticipantsId()
        .equals(leaderParticipants.getTeamParticipantsId())) {
      throw new CustomException(TEAM_PARTICIPANTS_EQUALS_EXCEPTION);
    }

    teamParticipantsRepository.delete(teamParticipants);

    return TeamKickOutResponse.builder()
        .teamId(request.getTeamId())
        .kickOutMemberId(teamParticipants.getMember().getMemberId())
        .nickName(teamParticipants.getTeamNickName())
        .message(KICK_OUT_TEAM_PARTICIPANTS)
        .build();
  }

  @Transactional
  public Team disbandTeam(Long userId, TeamDisbandRequest request) {

    TeamParticipants teamParticipants = teamParticipantsRepository
        .findByTeam_TeamIdAndMember_MemberId(request.getTeamId(), userId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    disbandCheckPermission(request.getPassword(), teamParticipants);

    Team team = teamParticipants.getTeam();

    isDeletedCheck(team);

    team.updateReservationTime();
    return team;
  }

  @Transactional
  public Team restoreTeam(Long userId, LocalDate restoreDt, Long teamId) {
    TeamParticipants teamParticipants = teamParticipantsRepository
        .findByTeam_TeamIdAndMember_MemberId(teamId, userId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    if (!teamParticipants.getTeamRole().equals(TeamRole.READER)) {
      throw new CustomException(TEAM_PARTICIPANTS_NOT_LEADER_EXCEPTION);
    }

    Team team = teamParticipants.getTeam();

    if (team.isDelete()) {
      throw new CustomException(TEAM_IS_DELETE_TRUE_EXCEPTION);
    }

    if (team.getRestorationDt() == null) {
      throw new CustomException(TEAM_NOT_DELETEING_EXCEPTION);
    } else if (!team.getRestorationDt().isAfter(restoreDt)) {
      team.updateIsDelete();
      return team;
    }

    team.deleteReservationTime();
    return team;
  }

  public boolean existById(Long teamId) {
    return teamRepository.existsById(teamId);
  }

  public Page<Team> getTeams(Long userId, Pageable pageable) {
    return teamRepository
        .findAllByTeamParticipants_Member_MemberIdAndIsDelete(
            userId, DELETE_FALSE_FLAG, pageable
        );
  }

  @Transactional
  public Team updateTeam(TeamUpdateRequest teamUpdateRequest, Long userId) {
    TeamParticipants teamParticipants = teamParticipantsRepository
        .findByTeam_TeamIdAndMember_MemberId(teamUpdateRequest.getTeamId(), userId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    if (!teamParticipants.getTeamRole().equals(TeamRole.READER)) {
      throw new CustomException(TEAM_PARTICIPANT_NOT_VALID_READER_EXCEPTION);
    }

    Team team = teamParticipants.getTeam();

    isDeletedCheck(team);
    String teamName = teamUpdateRequest.getTeamName() == null ? team.getName() : teamUpdateRequest.getTeamName();

    String url = imgStore.uploadImg(
        teamUpdateRequest.getProfileImg() ,ImgType.TEAM , teamName
        );
    team.updateNameOrProfileUrl(teamUpdateRequest.getTeamName(), url);
    return team;
  }

  public void isDeletedCheck(Team team) {
    if (!Objects.isNull(team.getRestorationDt())) {
      throw new CustomException(TEAM_IS_DELETEING_EXCEPTION);
    }

    if (team.isDelete()) {
      throw new CustomException(TEAM_IS_DELETE_TRUE_EXCEPTION);
    }
  }

  public void disbandCheckPermission(String password, TeamParticipants teamParticipants) {
    if (!teamParticipants.getTeamRole().equals(TeamRole.READER)) {
      throw new CustomException(TEAM_PARTICIPANTS_NOT_LEADER_EXCEPTION);
    }

    // todo 복호화 작업이 필요하다...ㅠㅠ
    if (!teamParticipants.getMember().getPassword()
        .equals(password)) {
      throw new CustomException(PASSWORD_NOT_MATCH_EXCEPTION);
    }
  }
}
