package com.api.backend.documents.service;

import com.api.backend.documents.data.dto.DeleteDocsResponse;
import com.api.backend.documents.data.dto.DocumentInitRequest;
import com.api.backend.documents.data.entity.Documents;
import com.api.backend.documents.data.repository.DocumentsRepository;
import com.api.backend.documents.valid.DocumentAndCommentValidCheck;
import com.api.backend.global.exception.CustomException;
import com.api.backend.team.data.entity.TeamParticipants;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
  private final DocumentsRepository documentsRepository;
  private final DocumentAndCommentValidCheck validCheck;

  public Page<Documents> getDocsList(Long teamId, Principal principal, Pageable pageable, LocalDate startDt, LocalDate endDt) {

    Long memberId = validCheck.getMemberId(principal);
    TeamParticipants teamParticipant = validCheck.findValidTeamParticipantByMemberId(memberId);

    validCheck.validTeamAndTeamParticipant(teamId, teamParticipant);

    Page<Documents> allDocsInTeam = null ;
    if (startDt == null && endDt == null) {
      return findAllDocumentInTeam(teamId, pageable, allDocsInTeam);
    }

    if (startDt != null && endDt == null) {
      return findAllDocumentInTeamWithStartDt(teamId, startDt.atStartOfDay(), pageable, allDocsInTeam);
    }

    if (startDt == null && endDt != null) {
      return findAllDocumentInTeamWithEndDt(teamId, endDt.plusDays(1).atStartOfDay(), pageable, allDocsInTeam);
    }

    if (startDt != null && endDt != null) {
      return findAllDocumentInTeamBetweenStartDtAndEndDt(teamId, startDt.atStartOfDay(),
          endDt.plusDays(1).atStartOfDay(), pageable, allDocsInTeam);
    }
    return Page.empty();
  }

  public Documents createDocs(DocumentInitRequest request, Long teamId, Principal principal) throws CustomException {
    Long memberId = validCheck.getMemberId(principal);
    TeamParticipants teamParticipant = validCheck.findValidTeamParticipantByMemberId(memberId);

    validCheck.validTeamAndTeamParticipant(teamId, teamParticipant);

    Documents saveDocuments = documentsRepository.save(Documents.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .writerId(teamParticipant.getTeamParticipantsId())
        .teamId(teamId)
        .build());

    return saveDocuments;
  }

  @Transactional
  public DeleteDocsResponse deleteDocs(Long teamId, String documentId, Principal principal) {

    Long memberId = validCheck.getMemberId(principal);
    TeamParticipants teamParticipant = validCheck.findValidTeamParticipantByMemberId(memberId);
    validCheck.validTeamAndTeamParticipant(teamId, teamParticipant);

    Documents documents = validCheck.findValidDocument(documentId);
    validCheck.validDocumentByTeamId(teamId, documents);
    validCheck.validDocumentByWriterId(teamParticipant, documents);
    documentsRepository.deleteById(documentId);

    return DeleteDocsResponse.builder()
        .id(documents.getId())
        .title(documents.getTitle())
        .message("삭제 되었습니다.")
        .build();
  }
  private Page<Documents> findAllDocumentInTeam(Long teamId, Pageable pageable, Page<Documents> allDocsInTeam) {
    allDocsInTeam = documentsRepository.findAllByTeamId(teamId, pageable);
    if (allDocsInTeam != null) {
      return allDocsInTeam;
    } else {
      return Page.empty();
    }
  }

  private Page<Documents> findAllDocumentInTeamWithStartDt(Long teamId, LocalDateTime startDt, Pageable pageable, Page<Documents> allDocsInTeam) {
    allDocsInTeam = documentsRepository.findAllByTeamIdAndCreatedDtGreaterThanEqual(teamId, startDt ,pageable);
    if (allDocsInTeam != null) {
      return allDocsInTeam;
    } else {
      return Page.empty();
    }
  }
  private Page<Documents> findAllDocumentInTeamWithEndDt(Long teamId, LocalDateTime endDt, Pageable pageable, Page<Documents> allDocsInTeam) {
    allDocsInTeam = documentsRepository.findAllByTeamIdAndCreatedDtLessThanEqual(teamId, endDt, pageable);
    if (allDocsInTeam != null) {
      return allDocsInTeam;
    } else {
      return Page.empty();
    }
  }

  private Page<Documents> findAllDocumentInTeamBetweenStartDtAndEndDt(Long teamId, LocalDateTime startDt, LocalDateTime endDt, Pageable pageable, Page<Documents> allDocsInTeam) {
    allDocsInTeam = documentsRepository.findAllByTeamIdAndCreatedDtBetween(teamId, startDt, endDt, pageable);
    if (allDocsInTeam != null) {
      return allDocsInTeam;
    } else {
      return Page.empty();
    }
  }
}
