package com.api.backend.comment.data.repository;


import com.api.backend.comment.data.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

  @Transactional
  void deleteById(String id);
}
