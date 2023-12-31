package com.api.backend.member.data.repository;

import com.api.backend.member.data.entity.Member;
import com.api.backend.member.data.type.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmailAndLoginType(String memberEmail, LoginType loginType);

    Optional<Member> findByLoginTypeAndSocialId(LoginType loginType, String socialId);

    List<Member> findAllByIsAuthenticatedEmailAndCreateDtBefore(Boolean emailAuthenticationYN, LocalDateTime nowMinusOneYear);
}
