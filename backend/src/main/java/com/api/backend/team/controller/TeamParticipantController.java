package com.api.backend.team.controller;

import com.api.backend.team.data.dto.TeamParticipantsDto;
import com.api.backend.team.service.TeamParticipantsService;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team/{teamId}/participant")
public class TeamParticipantController {

  private final TeamParticipantsService teamParticipantsService;

  @DeleteMapping
  public ResponseEntity<String> deleteTeamParticipantRequest(
      Principal principal,
      @PathVariable(value = "teamId") Long teamId
  ) {
    return ResponseEntity.ok(
        teamParticipantsService.deleteTeamParticipant(Long.valueOf(principal.getName()), teamId)
    );
  }

  @PatchMapping("/{participantId}")
  public ResponseEntity<String> updateRoleTeamParticipantRequest(
      Principal principal,
      @PathVariable(value = "participantId") Long participantId,
      @PathVariable(value = "teamId") Long teamId
  ) {
    return ResponseEntity.ok(
        teamParticipantsService.updateRoleTeamParticipant(Long.valueOf(principal.getName()), participantId, teamId)
    );
  }

  @GetMapping("/list")
  public ResponseEntity<List<TeamParticipantsDto>> getTeamParticipantsRequest(
      @PathVariable(value = "teamId") Long teamId,
      Principal principal
  ) {
    return ResponseEntity.ok(
        teamParticipantsService.getTeamParticipants(teamId, Long.valueOf(principal.getName()))
            .stream().map(TeamParticipantsDto::from)
            .collect(Collectors.toList())
    );
  }

  @GetMapping
  public ResponseEntity<TeamParticipantsDto> getTeamParticipantRequest(
      @PathVariable(value = "teamId") Long teamId,
      Principal principal
  ) {
    return ResponseEntity.ok(
        TeamParticipantsDto.from(
            teamParticipantsService.getTeamParticipant(teamId, Long.valueOf(principal.getName()))
        )
    );
  }
}
