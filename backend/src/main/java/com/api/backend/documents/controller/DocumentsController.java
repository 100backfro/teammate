package com.api.backend.documents.controller;

import com.api.backend.documents.data.dto.DocumentInitRequest;
import com.api.backend.documents.data.dto.DocumentResponse;
import com.api.backend.documents.data.entity.Documents;
import com.api.backend.documents.service.DocumentService;
import com.api.backend.notification.aop.annotation.TeamParticipantsSendNotify;
import com.api.backend.notification.transfers.TeamParticipantsNotifyByDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(tags = "문서")
@RequestMapping("/team/{teamId}/documents")
@RequiredArgsConstructor
public class DocumentsController {
  private final DocumentService documentService;

  @ApiOperation(value = "해당 팀의 전체 문서를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "해당 팀의 전체 문서를 가져왔습니다."),
      @ApiResponse(code = 404, message = "페이지를 찾을 수 없습니다."),
      @ApiResponse(code = 400, message = "CustomException을 반환합니다."),
  })
  @ApiImplicitParams(
      {
          @ApiImplicitParam(
              name = "access token"
              , value = "jwt access token"
              , required = true
              , dataType = "String"
              , paramType = "header"
              , defaultValue = "None"
          ),
          @ApiImplicitParam(
              name = "teamId"
              , value = "팀 id"
              , required = true
              , dataType = "Long"
              , paramType = "path"
              , defaultValue = "None"
              , example = "1")
      })
  @GetMapping()
  public ResponseEntity<List<DocumentResponse>> getDocsList(
        @PathVariable
        Long teamId,
        @ApiIgnore
        Principal principal,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDt) {
    List<Documents> docsList = documentService.getDocsList(teamId, principal, startDt, endDt);
    List<DocumentResponse> documentDtoList = docsList.stream()
        .map(document -> DocumentResponse.from(document))
        .collect(Collectors.toList());

    return ResponseEntity.ok(documentDtoList);
  }

  @ApiOperation(value = "해당 팀의 문서를 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "문서를 성공적으로 생성했습니다."),
      @ApiResponse(code = 404, message = "페이지를 찾을 수 없습니다."),
      @ApiResponse(code = 400, message = "CustomException을 반환합니다."),
  })
  @ApiImplicitParams(
      {
          @ApiImplicitParam(
              name = "access token"
              , value = "jwt access token"
              , required = true
              , dataType = "String"
              , paramType = "header"
              , defaultValue = "None"
          ),
          @ApiImplicitParam(
              name = "teamId"
              , value = "팀 id"
              , required = true
              , dataType = "Long"
              , paramType = "path"
              , defaultValue = "None"
              , example = "1")
            })
  @TeamParticipantsSendNotify
  @PostMapping()
  public ResponseEntity<TeamParticipantsNotifyByDto> createDocs(
          @PathVariable
          Long teamId,
          @RequestBody @Valid DocumentInitRequest request,
          @ApiIgnore
          Principal principal) {
    return ResponseEntity.ok(
        documentService.createDocs(request, teamId, principal)
    );
  }

  @ApiOperation(value = "해당 팀의 문서를 삭제했습니다.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "문서를 성공적으로 삭제했습니다."),
      @ApiResponse(code = 404, message = "페이지를 찾을 수 없습니다."),
      @ApiResponse(code = 400, message = "CustomException을 반환합니다."),
  })
  @ApiImplicitParams(
      {
          @ApiImplicitParam(
              name = "access token"
              , value = "jwt access token"
              , required = true
              , dataType = "String"
              , paramType = "header"
              , defaultValue = "None"
          ),

          @ApiImplicitParam(
              name = "teamId"
              , value = "팀 id"
              , required = true
              , dataType = "Long"
              , paramType = "path"
              , defaultValue = "None"
              , example = "1"
          )
          ,
          @ApiImplicitParam(
              name = "documentsId"
              , value = "문서 id"
              , required = true
              , dataType = "string"
              , paramType = "path"
              , defaultValue = "None"
              , example = "657595c6c97b622e0440f394"
          )
      })
  @TeamParticipantsSendNotify
  @DeleteMapping("/{documentsId}")
  public ResponseEntity<TeamParticipantsNotifyByDto> deleteDocs(
      @PathVariable
      Long teamId,
      @PathVariable
      String documentsId,
      @ApiIgnore
      Principal principal) {

    return ResponseEntity.ok()
        .body(documentService.deleteDocs(teamId, documentsId, principal));
  }
}
