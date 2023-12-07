package com.api.backend.team.controller;

import static com.api.backend.team.data.ResponseMessage.UPDATE_TEAM;
import static com.api.backend.team.data.ResponseMessage.UPDATE_TEAM_PARTICIPANTS;

import com.api.backend.team.data.dto.TeamCreateRequest;
import com.api.backend.team.data.dto.TeamCreateResponse;
import com.api.backend.team.data.dto.TeamDisbandRequest;
import com.api.backend.team.data.dto.TeamDisbandResponse;
import com.api.backend.team.data.dto.TeamDtoResponse;
import com.api.backend.team.data.dto.TeamKickOutRequest;
import com.api.backend.team.data.dto.TeamKickOutResponse;
import com.api.backend.team.data.dto.TeamRestoreResponse;
import com.api.backend.team.data.dto.TeamUpdateRequest;
import com.api.backend.team.data.dto.TeamParticipantsUpdateResponse;
import com.api.backend.team.data.dto.TeamUpdateResponse;
import com.api.backend.team.data.entity.Team;
import com.api.backend.team.service.TeamService;
import java.security.Principal;
import java.time.LocalDate;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

  private final TeamService teamService;

  @PostMapping
  public ResponseEntity<TeamCreateResponse> createTeamRequest(
      @RequestBody @Valid
      TeamCreateRequest teamRequest,
      Principal principal
  ) {
    return ResponseEntity.ok(
            teamService.createTeam(teamRequest,principal.getName())
    );
  }

  @GetMapping("/{teamId}/code")
  public ResponseEntity<String> getTeamUrlRequest(
      @PathVariable("teamId") Long teamId,
      Principal principal
  ) {
    return ResponseEntity.ok(
        teamService.getTeamUrl(teamId, principal.getName())
    );
  }

  @PostMapping("/{teamId}/{code}")
  public ResponseEntity<TeamParticipantsUpdateResponse> updateTeamParticipantRequest(
      @PathVariable("teamId") Long teamId,
      @PathVariable("code") String code,
      Principal principal
  ) {
    Team team = teamService.updateTeamParticipants(teamId, code, principal.getName());
    return ResponseEntity.ok(
        TeamParticipantsUpdateResponse
            .builder().teamName(team.getName())
            .teamId(teamId)
            .message(team.getName() + UPDATE_TEAM_PARTICIPANTS)
            .build()
    );
  }

  @PostMapping("/kick-out")
  public ResponseEntity<TeamKickOutResponse> kickOutTeamParticipantsRequest(
      @RequestBody @Valid
      TeamKickOutRequest teamKickOutRequest,
      Principal principal
  ) {
    return ResponseEntity.ok(
        teamService.kickOutTeamParticipants(teamKickOutRequest, principal.getName())
    );
  }

  @PutMapping("/disband")
  public ResponseEntity<TeamDisbandResponse> disbandTeamRequest(
      @RequestBody @Valid TeamDisbandRequest request,
      Principal principal
  ) {
    return ResponseEntity.ok(
        TeamDisbandResponse.from(
            teamService.disbandTeam(principal.getName(), request)
        )
    );
  }

  @PatchMapping("/{teamId}/restore")
  public ResponseEntity<TeamRestoreResponse> restoreTeamRequest(
      Principal principal,
      @RequestParam(value = "restoreDt") @DateTimeFormat(pattern = "yyyy-MM-dd")
      LocalDate restoreDt,
      @PathVariable("teamId") Long teamId
  ) {
    return ResponseEntity.ok(
        TeamRestoreResponse.from(
            teamService.restoreTeam(principal.getName(), restoreDt, teamId)
        )
    );
  }

  @GetMapping("/list")
  public ResponseEntity<Page<TeamDtoResponse>> getTeamsRequest(
      Principal principal,
      Pageable pageable
  ){
    return ResponseEntity.ok(
        TeamDtoResponse.fromDtos(
            teamService.getTeams(principal.getName(), pageable)
        )
    );
  }

  @PutMapping
  public ResponseEntity<TeamUpdateResponse> updateTeamRequest(
      @RequestBody @Valid TeamUpdateRequest teamUpdateRequest,
      @RequestParam(value = "userId") String userId
  ) {
    return ResponseEntity.ok(
        TeamUpdateResponse.from(
            teamService.updateTeam(teamUpdateRequest, userId)
        )
    );
  }



}