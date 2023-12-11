package com.api.backend.documents.data.repository;

import com.api.backend.documents.data.entity.Documents;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DocumentsRepository extends MongoRepository<Documents, String> {
  @Override
  Page<Documents> findAll(Pageable pageable);


  @Transactional
  void deleteById(String documentId);

  Page<Documents> findAllByTeamId(Long teamId, Pageable pageable);

  Page<Documents> findAllByTeamIdAndCreatedDtGreaterThanEqual(Long teamId, LocalDateTime startDt, Pageable pageable);

  Page<Documents> findAllByTeamIdAndCreatedDtLessThanEqual(Long teamId, LocalDateTime endDt, Pageable pageable);

  Page<Documents> findAllByTeamIdAndCreatedDtBetween(Long teamId, LocalDateTime startDt, LocalDateTime endDt, Pageable pageable);
}
