package com.api.backend.comment.controller;

import com.api.backend.comment.data.dto.CommentEditRequest;
import com.api.backend.comment.data.dto.CommentInitRequest;
import com.api.backend.comment.data.dto.CommentResponse;
import com.api.backend.comment.data.dto.DeleteCommentsResponse;
import com.api.backend.comment.data.entity.Comment;
import com.api.backend.comment.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(tags = "댓글")
@RequiredArgsConstructor
@RequestMapping("/team/{teamId}/documents/{documentId}/comments")
public class CommentController {

  private final CommentService commentService;

  @ApiOperation(value = "해당 문서의 댓글을 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "해당 문서의 댓글을 생성했습니다."),
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
              , example = "1"),

          @ApiImplicitParam(
              name = "documentId"
              , value = "문서 id"
              , required = true
              , dataType = "String"
              , paramType = "path"
              , defaultValue = "None"
              , example = "657595c6c97b622e0440f394")
      })
  @PostMapping()
  public ResponseEntity<CommentResponse> createComments(
      @PathVariable Long teamId, @PathVariable String documentId,
      @RequestBody @Valid CommentInitRequest request,
      @ApiIgnore
      Principal principal
  ) {
    Comment comment = commentService.createComment(teamId, documentId, request, principal);

    return ResponseEntity.ok(CommentResponse.from(comment));
  }

  @ApiOperation(value = "해당 문서의 댓글을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "해당 문서의 전체 댓글을 가져왔습니다."),
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
              , example = "1"),

          @ApiImplicitParam(
              name = "documentId"
              , value = "문서 id"
              , required = true
              , dataType = "String"
              , paramType = "path"
              , defaultValue = "None"
              , example = "657595c6c97b622e0440f394")
      })
  @GetMapping()
  public ResponseEntity<Page<CommentResponse>> getCommentsList(
      @PathVariable Long teamId, @PathVariable String documentId,
      @ApiIgnore
      Principal principal,
      Pageable pageable) {
    Page<Comment> commentPage = commentService.getCommentList(teamId, documentId, principal, pageable);
    Page<CommentResponse> commentDtoPage = commentPage.map(
        comment -> CommentResponse.from(comment)
    );

    return ResponseEntity.ok(commentDtoPage);
  }

  @ApiOperation(value = "해당 문서의 댓글을 수정합니다.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "해당 문서의 댓글을 수정했습니다."),
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
              , example = "1"),

          @ApiImplicitParam(
              name = "documentId"
              , value = "문서 id"
              , required = true
              , dataType = "String"
              , paramType = "path"
              , defaultValue = "None"
              , example = "657595c6c97b622e0440f394"),
          @ApiImplicitParam(
              name = "commentId"
              , value = "댓글 id"
              , required = true
              , dataType = "String"
              , paramType = "path"
              , defaultValue = "None"
              , example = "6575d6fe99101a62f9710877")
      })
  @PutMapping("/{commentId}")
  public ResponseEntity<CommentResponse> editComment(
      @PathVariable Long teamId, @PathVariable String documentId,
      @PathVariable String commentId,
      @RequestBody @Valid CommentEditRequest request,
      @ApiIgnore
      Principal principal
  ) {
    Comment comment = commentService.editComment(teamId, documentId, commentId, request, principal);

    return ResponseEntity.ok(CommentResponse.from(comment));
  }

  @ApiOperation(value = "해당 문서의 댓글을 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "해당 문서의 댓글을 삭제했습니다."),
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
              , example = "1"),

          @ApiImplicitParam(
              name = "documentId"
              , value = "문서 id"
              , required = true
              , dataType = "String"
              , paramType = "path"
              , defaultValue = "None"
              , example = "657595c6c97b622e0440f394"),
          @ApiImplicitParam(
              name = "commentId"
              , value = "댓글 id"
              , required = true
              , dataType = "String"
              , paramType = "path"
              , defaultValue = "None"
              , example = "6575d6fe99101a62f9710877")
      })
  @DeleteMapping("/{commentId}")
  public ResponseEntity<DeleteCommentsResponse> deleteComment(
      @PathVariable Long teamId, @PathVariable String documentId,
      @PathVariable String commentId,
      Principal principal
  ) {

    return ResponseEntity.ok(commentService.deleteComment(teamId, documentId, commentId, principal));
  }
}
