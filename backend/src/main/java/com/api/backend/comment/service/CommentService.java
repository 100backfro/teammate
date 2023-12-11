package com.api.backend.comment.service;

import com.api.backend.comment.data.dto.CommentEditRequest;
import com.api.backend.comment.data.dto.CommentInitRequest;
import com.api.backend.comment.data.dto.DeleteCommentsResponse;
import com.api.backend.comment.data.entity.Comment;
import com.api.backend.comment.data.repository.CommentRepository;
import com.api.backend.documents.data.entity.Documents;
import com.api.backend.documents.data.repository.DocumentsRepository;
import com.api.backend.documents.valid.DocumentAndCommentValidCheck;
import com.api.backend.team.data.entity.TeamParticipants;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final CommentRepository commentRepository;
  private final DocumentsRepository documentsRepository;
  private final DocumentAndCommentValidCheck validCheck;


  @Transactional
  public Comment createComment(Long teamId, String documentId, CommentInitRequest commentInitRequest, Principal principal) {

    Long memberId = validCheck.getMemberId(principal);
    TeamParticipants teamParticipants = validCheck.findValidTeamParticipantByMemberId(memberId);

    Documents validDocument = validCheck.findValidDocument(documentId);

    validCheck.validDocumentByTeamId(teamId, validDocument);

    Comment comment = commentRepository.save(Comment.builder()
        .teamId(teamId)
        .content(commentInitRequest.getContent())
        .writerId(commentInitRequest.getWriterId())
        .build());

    validDocument.getCommentIds().add(comment);
    documentsRepository.save(validDocument);
    return comment;
  }

  public Page<Comment> getCommentList(Long teamId, String documentId,  Principal principal, Pageable pageable) {

    Long memberId = validCheck.getMemberId(principal);
    TeamParticipants teamParticipants = validCheck.findValidTeamParticipantByMemberId(memberId);
    Documents documents = validCheck.findValidDocument(documentId);
    validCheck.validDocumentByTeamId(teamId, documents);
    List<Comment> commentIds = documents.getCommentIds();

    if (commentIds != null && !commentIds.isEmpty()) {
      int start = (int) pageable.getOffset();
      int end = Math.min((start + pageable.getPageSize()), commentIds.size());

      List<Comment> sublist = commentIds.subList(start, end);
      return new PageImpl<>(sublist, pageable, commentIds.size());
    }
    return Page.empty();
  }

  @Transactional
  public Comment editComment(Long teamId, String documentId, String commentId, CommentEditRequest commentEditRequest, Principal principal) {

    Long memberId = validCheck.getMemberId(principal);
    TeamParticipants teamParticipants = validCheck.findValidTeamParticipantByMemberId(memberId);
    Documents documents = validCheck.findValidDocument(documentId);
    validCheck.validDocumentByTeamId(teamId, documents);

    Comment comment = validCheck.findValidComment(commentId);

    validCheck.validCommentByWrterId(comment, commentEditRequest.getEditorId());

    return commentRepository.save(Comment.builder()
        .id(commentId)
        .writerId(commentEditRequest.getEditorId())
        .content(commentEditRequest.getContent())
        .teamId(teamId)
        .createdDt(comment.getCreatedDt())
        .build());
  }

  @Transactional
  public DeleteCommentsResponse deleteComment(Long teamId, String documentId, String commentId, Principal principal) {

    Long memberId = validCheck.getMemberId(principal);
    TeamParticipants teamParticipants = validCheck.findValidTeamParticipantByMemberId(memberId);

    Documents documents = validCheck.findValidDocument(documentId);

    validCheck.validDocumentByTeamId(teamId, documents);

    Comment comment = validCheck.findValidComment(commentId);

    validCheck.validCommentByWrterId(comment, teamParticipants.getTeamParticipantsId());

    commentRepository.deleteById(commentId);
    documents.getCommentIds().removeIf(deletedComment -> deletedComment.getId().equals(commentId));
    documentsRepository.save(documents);

    return DeleteCommentsResponse.builder()
        .id(commentId)
        .writerId(comment.getWriterId())
        .content(comment.getContent())
        .message("해당 댓글이 삭제 되었습니다.")
        .build();
  }
}
