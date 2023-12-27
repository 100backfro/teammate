package com.api.backend.team.data.repository;

import com.api.backend.team.data.entity.TeamParticipants;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamParticipantsRepository extends JpaRepository<TeamParticipants,Long> {

  Optional<TeamParticipants> findByTeam_TeamIdAndMember_MemberId(Long teamId, Long userId);

  boolean existsByTeam_TeamIdAndMember_MemberId(Long teamId,Long userId);
  boolean existsByMember_MemberId(Long userId);

  List<TeamParticipants> findAllByMember_MemberIdAndTeam_IsDeleteAndTeam_RestorationDtIsNull
      (Long memberId, boolean flag);
  List<TeamParticipants> findAllByMember_MemberIdAndTeam_IsDelete(Long memberId, boolean flag);
  Optional<TeamParticipants> findByMember_Email(String email);
  Optional<TeamParticipants> findByMember_MemberId(Long memberId);
  Optional<TeamParticipants> findByMember_MemberIdAndTeam_TeamId(Long memberId, Long teamId);

  List<TeamParticipants> findByTeam_TeamIdAndTeamParticipantsIdNot(Long teamId, Long teamParticipantsId);

  List<TeamParticipants> findAllByTeam_TeamIdAndMember_MemberIdNot(Long teamId, Long memberId);
}
