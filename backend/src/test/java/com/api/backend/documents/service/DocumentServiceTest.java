package com.api.backend.documents.service;

import static com.api.backend.global.exception.type.ErrorCode.DOCUMENT_WRITER_EXISTS_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.DOCUMENT_WRITER_UNMATCH_TEAM_PARTICIPANTS_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION;
import static com.api.backend.global.exception.type.ErrorCode.TEAM_PARTICIPANTS_NOT_VALID_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.api.backend.documents.data.dto.DeleteDocsResponse;
import com.api.backend.documents.data.dto.DocumentInitRequest;
import com.api.backend.documents.data.entity.Documents;
import com.api.backend.documents.data.repository.DocumentsRepository;
import com.api.backend.documents.valid.DocumentAndCommentValidCheck;
import com.api.backend.global.exception.CustomException;
import com.api.backend.member.data.entity.Member;
import com.api.backend.team.data.entity.Team;
import com.api.backend.team.data.entity.TeamParticipants;
import com.api.backend.team.data.type.TeamRole;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  @Mock
  private DocumentAndCommentValidCheck validCheck;

  @Mock
  private DocumentsRepository documentsRepository;

  @InjectMocks
  private DocumentService documentService;

  //  @Test
//  @DisplayName("문서 전체 조회(문서가 존재할 때)_startDt == null && endDt == null")
//  void getDocsListWhenDocsExists1() {
//      //given
//    Documents document = createtestDocuments();
//    TeamParticipants teamParticipants = createTestSetting();
//    Principal principal = Mockito.mock(Principal.class);
//    Pageable pageable = PageRequest.of(0, 4, Sort.unsorted());
//    Page<Documents> documentsPage = new PageImpl<>(Collections.singletonList(document));
//    when(validCheck.getMemberId(principal)).thenReturn(1L);
//    when(validCheck.findValidTeamParticipantByMemberId(1L)).thenReturn(teamParticipants);
//    when(documentsRepository.findAllByTeamId(2L, pageable)).thenReturn(documentsPage);
//
//    //when
//    LocalDate startDt = null;
//    LocalDate endDt = null;
//    Page<Documents> docsPage = documentService.getDocsList(2L, principal, pageable, startDt, endDt);
//
//    //then
//    assertNotNull(docsPage);
//    assertFalse(docsPage.isEmpty());
//    assertEquals(1, docsPage.getContent().size());
//    assertEquals("testId", docsPage.getContent().get(0).getId());
//  }
//
//  @Test
//  @DisplayName("문서 전체 조회(문서가 존재할 때)_startDt != null && endDt == null")
//  void getDocsListWhenDocsExists2() {
//    // startDt= 11월 1일
//    //given
//    LocalDateTime specificDate = LocalDateTime.of(2023, 11, 1, 0, 0, 0);
//    Documents documentWithSpecificDate = createDocumentWithSpecificDate(specificDate);
//    TeamParticipants teamParticipants = createTestSetting();
//
//    Pageable pageable = PageRequest.of(0, 4, Sort.unsorted());
//    Page<Documents> documentsPage = new PageImpl<>(Collections.singletonList(documentWithSpecificDate));
//
//    Principal principal = Mockito.mock(Principal.class);
//    when(validCheck.getMemberId(principal)).thenReturn(1L);
//    when(validCheck.findValidTeamParticipantByMemberId(1L)).thenReturn(teamParticipants);
//    LocalDate startDt = LocalDate.of(2023, 11, 1);
//    when(documentsRepository.findAllByTeamIdAndCreatedDtGreaterThanEqual(2L, startDt.atStartOfDay(), pageable)).thenReturn(documentsPage);
//
//    //when
//
//    LocalDate endDt = null;
//    Page<Documents> docsPage = documentService.getDocsList(2L, principal, pageable, startDt, endDt);
//
//    //then
//    assertNotNull(docsPage);
//    assertFalse(docsPage.isEmpty());
//    assertEquals(1, docsPage.getContent().size());
//    assertEquals("testId", docsPage.getContent().get(0).getId());
//  }
//
//
//  @Test
//  @DisplayName("문서 전체 조회(문서가 존재할 때)_startDt != null && endDt != null")
//  void getDocsListWhenDocsExists3() {
//    // startDt= 11월 1일
//    //given
//    LocalDateTime specificDate = LocalDateTime.of(2023, 11, 1, 0, 0, 0);
//    Documents documentWithSpecificDate = createDocumentWithSpecificDate(specificDate);
//    TeamParticipants teamParticipants = createTestSetting();
//
//    Pageable pageable = PageRequest.of(0, 4, Sort.unsorted());
//    Page<Documents> documentsPage = new PageImpl<>(Collections.singletonList(documentWithSpecificDate));
//
//    Principal principal = Mockito.mock(Principal.class);
//    when(validCheck.getMemberId(principal)).thenReturn(1L);
//    when(validCheck.findValidTeamParticipantByMemberId(1L)).thenReturn(teamParticipants);
//    LocalDate startDt = LocalDate.of(2023, 10, 1);
//    LocalDate endDt =  LocalDate.of(2023, 12, 1);;
//    when(documentsRepository.findAllByTeamIdAndCreatedDtBetween(2L, startDt.atStartOfDay(), endDt.plusDays(1).atStartOfDay(), pageable)).thenReturn(documentsPage);
//
//    //when
//
//    Page<Documents> docsPage = documentService.getDocsList(2L, principal, pageable, startDt, endDt);
//
//    //then
//    assertNotNull(docsPage);
//    assertFalse(docsPage.isEmpty());
//    assertEquals(1, docsPage.getContent().size());
//    assertEquals("testId", docsPage.getContent().get(0).getId());
//  }
//
//  @Test
//  @DisplayName("문서 전체 조회(문서가 존재하지 않을 때)_startDt = null && endDt == null")
//  void getDocsListWhenDocsNotExists() {
//    //given
//    TeamParticipants teamParticipants = createTestSetting();
//    Principal principal = Mockito.mock(Principal.class);
//    when(validCheck.getMemberId(principal)).thenReturn(1L);
//    when(validCheck.findValidTeamParticipantByMemberId(1L)).thenReturn(teamParticipants);
//    //when
//    Pageable pageable = PageRequest.of(0, 4, Sort.unsorted());
//    LocalDate startDt = null;
//    LocalDate endDt = null;
//    Page<Documents> docsPage = documentService.getDocsList(2L, principal, pageable, startDt, endDt);
//
//    //then
//    assertNotNull(docsPage);
//    assertTrue(docsPage.isEmpty());
//  }
//
//  @Test
//  @DisplayName("문서 초기 생성 성공")
//  void createDocs_Success() {
//    //given
//    DocumentInitRequest request = createTestDocumentsRequest();
//    TeamParticipants teamParticipants = createTestSetting();
//    Documents documents = createtestDocuments();
//
//    Principal principal = Mockito.mock(Principal.class);
//    when(documentsRepository.save(any())).thenReturn(documents);
//    when(validCheck.getMemberId(principal)).thenReturn(1L);
//    when(validCheck.findValidTeamParticipantByMemberId(1L)).thenReturn(teamParticipants);
//
//    //when
//    Documents savedDocuments = documentService.createDocs(request, 2L, principal);
//
//    //then
//    assertEquals(2L, savedDocuments.getTeamId());
//    assertEquals("testTitle", savedDocuments.getTitle());
//    assertEquals("testContent", savedDocuments.getContent());
//
//  }
//
//
//  @Test
//  @DisplayName("문서 초기 생성 실패_teamParticipants가 아닐 경우")
//  void createDocs_Fail_By_Not_Exist() {
//    //given
//    DocumentInitRequest request = createTestDocumentsRequest();
//    createTestSetting();
//    TeamParticipants testSettingFail = createTestSetting_Fail();
//
//    Principal principal = Mockito.mock(Principal.class);
//    when(validCheck.getMemberId(principal)).thenReturn(1L);
//    doThrow(new CustomException(TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION)).when(validCheck)
//        .findValidTeamParticipantByMemberId(1L);
//    //when
//    CustomException exception = assertThrows(CustomException.class, () -> documentService.createDocs(request, 2L, principal));
//
//    //then
//    assertEquals(exception.getErrorCode(), TEAM_PARTICIPANTS_NOT_FOUND_EXCEPTION);
//
//  }
//
//  @Test
//  @DisplayName("문서 초기 생성 실패_teamId가 다를 경우")
//  void createDocs_Fail_By_UnMatch_TeamId() {
//    //given
//    DocumentInitRequest request = createTestDocumentsRequest();
//    TeamParticipants teamParticipants = createTestSetting_Fail();
//    Principal principal = Mockito.mock(Principal.class);
//    when(validCheck.getMemberId(principal)).thenReturn(1L);
//    doThrow(new CustomException(TEAM_PARTICIPANTS_NOT_VALID_EXCEPTION)).when(validCheck)
//        .validTeamAndTeamParticipant(1L, teamParticipants);
//    //when
//    CustomException exception = assertThrows(CustomException.class, () -> documentService.createDocs(request, 213L, principal));
//
//    //then
//    assertEquals(exception.getErrorCode(), TEAM_PARTICIPANTS_NOT_VALID_EXCEPTION);
//  }
//
  @Test
  @DisplayName("문서 삭제 성공: 문서 작성자 본인일 때")
  void deleteDocs_Success() {
    //given
    Principal principal = Mockito.mock(Principal.class);
    Documents documents = createtestDocuments();
    TeamParticipants teamParticipants = createTestSetting_MATE();

    when(validCheck.getMemberId(principal)).thenReturn(1L);
    when(validCheck.findValidTeamParticipantByMemberIdAndTeamId(1L, 2L)).thenReturn(
        teamParticipants);
    when(validCheck.findValidDocument("testId")).thenReturn(documents);
    //when
    DeleteDocsResponse deleteDocsResponse = documentService.deleteDocs(2L, "testId", principal);

    //then
    assertNotNull(deleteDocsResponse);
    assertEquals(deleteDocsResponse.getId(), documents.getId());
    assertEquals(deleteDocsResponse.getTitle(), documents.getTitle());
  }

  @Test
  @DisplayName("문서 삭제 성공: 문서 작성자가 팀내에 없는 경우 팀장이 삭제")
  void deleteDocs_Success_By_LEADER() {
    //given
    Principal principal = Mockito.mock(Principal.class);
    Documents documents = createtestDocuments();
    TeamParticipants teamParticipants = createTestSetting_LEADER();

    when(validCheck.getMemberId(principal)).thenReturn(1L);
    when(validCheck.findValidTeamParticipantByMemberIdAndTeamId(1L, 2L)).thenReturn(
        teamParticipants);
    when(validCheck.findValidDocument("testId")).thenReturn(documents);

    //when
    DeleteDocsResponse deleteDocsResponse = documentService.deleteDocs(2L, "testId", principal);

    //then
    assertNotNull(deleteDocsResponse);
    assertEquals(deleteDocsResponse.getId(), documents.getId());
    assertEquals(deleteDocsResponse.getTitle(), documents.getTitle());
  }

  @Test
  @DisplayName("문서 삭제 실패: 팀내에 문서 생성자가 존재하고 문서 생성자가 아닐때")
  void deleteDocs_Fail_By_WriterExist_AND_LEADER() {
    //given
    Principal principal = Mockito.mock(Principal.class);
    Documents documents = createtestDocuments();
    TeamParticipants failTeamParticipants = createTestSetting_Fail_LEADER();

    when(validCheck.getMemberId(principal)).thenReturn(1L);
    when(validCheck.findValidTeamParticipantByMemberIdAndTeamId(1L, 2L)).thenReturn(
        failTeamParticipants);
    when(validCheck.findValidDocument("testId")).thenReturn(documents);


    doThrow(new CustomException(DOCUMENT_WRITER_EXISTS_EXCEPTION))
        .when(validCheck)
        .validWriterStatus(1L);

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> documentService.deleteDocs(2L, "testId", principal));

    //then
    assertEquals(exception.getErrorCode(), DOCUMENT_WRITER_EXISTS_EXCEPTION);
  }

  @Test
  @DisplayName("문서 삭제 실패: 문서 작성자가 아닌 팀원 일 경우")
  void deleteDocs_Fail_By_MATE() {
    //given
    Principal principal = Mockito.mock(Principal.class);
    Documents documents = createtestDocuments();
    TeamParticipants failTeamParticipants = createTestSetting_Fail_MATE();

    when(validCheck.getMemberId(principal)).thenReturn(1L);
    when(validCheck.findValidTeamParticipantByMemberIdAndTeamId(1L, 2L)).thenReturn(
        failTeamParticipants);
    when(validCheck.findValidDocument("testId")).thenReturn(documents);

    doThrow(new CustomException(DOCUMENT_WRITER_UNMATCH_TEAM_PARTICIPANTS_EXCEPTION))
        .when(validCheck)
        .validDocumentByWriterId(1L, 23L);

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> documentService.deleteDocs(2L, "testId", principal));

    //then
    assertEquals(exception.getErrorCode(), DOCUMENT_WRITER_UNMATCH_TEAM_PARTICIPANTS_EXCEPTION);
  }

  private Documents createtestDocuments() {
    return Documents.builder()
        .id("testId")
        .title("testTitle")
        .content("testContent")
        .writerId(1L) // participantsId
        .teamId(2L)
        .createdDt(LocalDateTime.now())
        .updatedDt(LocalDateTime.now())
        .build();
  }

  private TeamParticipants createTestSetting_MATE() {
    Team team = Team.builder()
        .teamId(2L)
        .build();

    Member member = Member.builder()
        .memberId(1L)
        .email("first_test@Email.com")
        .build();

    TeamParticipants teamParticipants = TeamParticipants.builder()
        .team(team)
        .member(member)
        .teamParticipantsId(1L)
        .teamRole(TeamRole.MATE)
        .build();

    return teamParticipants;
  }

  private TeamParticipants createTestSetting_LEADER() {
    Team team = Team.builder()
        .teamId(2L)
        .build();

    Member member = Member.builder()
        .memberId(1L)
        .email("first_test@Email.com")
        .build();

    TeamParticipants teamParticipants = TeamParticipants.builder()
        .team(team)
        .member(member)
        .teamParticipantsId(1L)
        .teamRole(TeamRole.LEADER)
        .build();

    return teamParticipants;
  }

  private TeamParticipants createTestSetting_Fail_LEADER() {
    Team team = Team.builder()
        .teamId(2L)
        .build();

    Member member = Member.builder()
        .memberId(2L)
        .email("second_test@Email.com")
        .build();

    TeamParticipants teamParticipants = TeamParticipants.builder()
        .team(team)
        .member(member)
        .teamParticipantsId(123L)
        .teamRole(TeamRole.LEADER)
        .build();

    return teamParticipants;
  }

  private TeamParticipants createTestSetting_Fail_MATE() {
    Team team = Team.builder()
        .teamId(2L)
        .build();

    Member member = Member.builder()
        .memberId(1L)
        .email("first_test@Email.com")
        .build();

    TeamParticipants teamParticipants = TeamParticipants.builder()
        .team(team)
        .member(member)
        .teamParticipantsId(23L)
        .teamRole(TeamRole.MATE)
        .build();

    return teamParticipants;
  }

  private DocumentInitRequest createTestDocumentsRequest() {
    return DocumentInitRequest.builder()
        .title("testTitle")
        .content("testContent")
        .writerEmail("test@Email.com")
        .build();
  }

  private Documents createDocumentWithSpecificDate(LocalDateTime specificDate) {

    return Documents.builder()
        .id("testId")
        .title("testTitle")
        .content("testContent")
        .writerId(1L)
        .teamId(2L)
        .createdDt(specificDate)
        .updatedDt(LocalDateTime.now())
        .build();
  }
}
