package com.api.backend.team.data.repository;

import com.api.backend.team.data.entity.TeamParticipants;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamParticipantsRepository extends JpaRepository<TeamParticipants,Long> {

  Optional<TeamParticipants> findByTeam_TeamIdAndMember_MemberId(Long teamId, Long userId);

  boolean existsByTeam_TeamIdAndMember_MemberId(Long teamId,Long userId);

  Page<TeamParticipants> findAllByMember_MemberIdAndTeam_IsDelete(Long memberId, boolean flag, Pageable pageable);

  Optional<TeamParticipants> findByMember_Email(String email);
  Optional<TeamParticipants> findByMember_MemberId(Long memberId);

  List<TeamParticipants> findByTeam_TeamIdAndTeamParticipantsIdNot(Long teamId, Long teamParticipantsId);

}
