package com.api.backend.team.service;

import static com.api.backend.global.exception.type.ErrorCode.MEMBER_NOT_EQUALS_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_NOT_EQUALS_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANT_DELETE_NOT_VALID_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANT_NOT_VALID_MATE_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANT_NOT_VALID_READER_EXCEPTION;
import static com.api.backend.team.data.ResponseMessage.DELETE_TEAM_PARTICIPANT;
import static com.api.backend.team.data.ResponseMessage.UPDATE_ROLE_TEAM_PARTICIPANT;

import com.api.backend.global.exception.CustomException;
import com.api.backend.member.data.dto.TeamParticipantUpdateRequest;
import com.api.backend.team.data.entity.Team;
import com.api.backend.team.data.entity.TeamParticipants;
import com.api.backend.team.data.repository.TeamParticipantsRepository;
import com.api.backend.team.data.type.ImgType;
import com.api.backend.team.data.type.TeamRole;
import com.api.backend.team.service.file.impl.ImgStoreImpl;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamParticipantsService {

  private final TeamService teamService;
  private final TeamParticipantsRepository teamParticipantsRepository;
  private final boolean DELETE_FALSE_FLAG = false;
  private final ImgStoreImpl imgStore;

  public String deleteTeamParticipant(Long userId, Long teamId) {
    TeamParticipants teamParticipants = teamParticipantsRepository
        .findByTeam_TeamIdAndMember_MemberId(teamId, userId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    if (teamParticipants.getTeamRole().equals(TeamRole.READER)) {
      throw new CustomException(TEAM_PARTICIPANT_DELETE_NOT_VALID_EXCEPTION);
    }
    teamParticipantsRepository.delete(teamParticipants);

    return DELETE_TEAM_PARTICIPANT;
  }

  @Transactional
  public String updateRoleTeamParticipant(Long userId, Long participantId, Long teamId) {
    TeamParticipants readerParticipant = teamParticipantsRepository
        .findByTeam_TeamIdAndMember_MemberId(teamId, userId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    if (!readerParticipant.getTeamRole().equals(TeamRole.READER)) {
      throw new CustomException(TEAM_PARTICIPANT_NOT_VALID_READER_EXCEPTION);
    }

    TeamParticipants mateParticipant = teamParticipantsRepository.findById(participantId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    if (!readerParticipant.getTeam().getTeamId()
        .equals(mateParticipant.getTeam().getTeamId())){
      throw new CustomException(TEAM_NOT_EQUALS_EXCEPTION);
    }
    if (!mateParticipant.getTeamRole().equals(TeamRole.MATE)) {
      throw new CustomException(TEAM_PARTICIPANT_NOT_VALID_MATE_EXCEPTION);
    }

    readerParticipant.updateRole(TeamRole.MATE);
    mateParticipant.updateRole(TeamRole.READER);
    return UPDATE_ROLE_TEAM_PARTICIPANT;
  }

  public List<TeamParticipants> getTeamParticipants(Long teamId, Long userId) {
    TeamParticipants teamParticipants = teamParticipantsRepository
        .findByTeam_TeamIdAndMember_MemberId(teamId, userId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    Team team = teamParticipants.getTeam();

    teamService.isDeletedCheck(team);

    return team.getTeamParticipants();
  }

  public TeamParticipants getTeamParticipant(Long teamId, Long userId) {
    TeamParticipants teamParticipants = teamParticipantsRepository
        .findByTeam_TeamIdAndMember_MemberId(teamId, userId)
        .orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    teamService.isDeletedCheck(teamParticipants.getTeam());

    return teamParticipants;
  }

  public Page<TeamParticipants> getTeamParticipantsByUserId(Principal principal, Pageable pageable) {
    return teamParticipantsRepository
        .findAllByMember_MemberIdAndTeam_IsDelete(
        Long.valueOf(principal.getName()),DELETE_FALSE_FLAG, pageable
    );
  }

  @Transactional
  public TeamParticipants updateParticipantContent(
      TeamParticipantUpdateRequest teamParticipantUpdateRequest, String userId
  ) {
    TeamParticipants teamParticipant = teamParticipantsRepository.findById(
        teamParticipantUpdateRequest.getTeamParticipantsId()
    ).orElseThrow(() -> new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION));

    teamService.isDeletedCheck(teamParticipant.getTeam());

    if (!teamParticipant.getMember().getMemberId()
        .equals(Long.valueOf(userId))) {
      throw new CustomException(MEMBER_NOT_EQUALS_EXCEPTION);
    }
    String participantNickName = teamParticipant.getTeamNickName();
    if (!Objects.equals(teamParticipantUpdateRequest.getTeamNickName(), "")) {
      teamParticipant.changeNickName(teamParticipantUpdateRequest.getTeamNickName());
      participantNickName = teamParticipantUpdateRequest.getTeamNickName();
    }

    if (teamParticipantUpdateRequest.getParticipantImg() != null) {
      teamParticipant.changeProfileUrl(
          imgStore.uploadImg(
              teamParticipantUpdateRequest.getParticipantImg(), ImgType.PARTICIPANT, participantNickName
          )
      );
    }
    return teamParticipant;
  }

  public List<TeamParticipants> getTeamParticipantsExcludeId(Long teamParticipantId, Long teamId) {
    return teamParticipantsRepository.findByTeam_TeamIdAndTeamParticipantsIdNot(teamId ,teamParticipantId);
  }
}
