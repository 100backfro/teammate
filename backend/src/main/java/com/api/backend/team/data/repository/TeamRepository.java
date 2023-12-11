package com.api.backend.team.data.repository;

import com.api.backend.team.data.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {

  boolean existsByTeamIdAndIsDelete(Long teamId, boolean isDelete);

  Page<Team> findAllByTeamParticipants_Member_MemberIdAndIsDelete(Long memberId, boolean isDelete, Pageable pageable);
}
