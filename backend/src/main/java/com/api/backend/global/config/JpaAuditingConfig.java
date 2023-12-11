package com.api.backend.global.config;

import com.api.backend.category.data.repository.ScheduleCategoryRepository;
import com.api.backend.member.data.repository.MemberRepository;
import com.api.backend.notification.data.repository.NotificationRepository;
import com.api.backend.schedule.data.repository.ScheduleRepository;
import com.api.backend.team.data.repository.TeamParticipantsRepository;
import com.api.backend.team.data.repository.TeamRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackageClasses = {MemberRepository.class, ScheduleRepository.class,
    TeamParticipantsRepository.class, NotificationRepository.class, TeamRepository.class, ScheduleCategoryRepository.class})
public class JpaAuditingConfig {

}
