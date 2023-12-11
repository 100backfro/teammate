package com.api.backend.category.data.repository;

import com.api.backend.category.data.entity.ScheduleCategory;
import com.api.backend.category.type.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleCategoryRepository extends JpaRepository<ScheduleCategory, Long> {

  Page<ScheduleCategory> findAllByCategoryTypeAndTeam_TeamId(CategoryType categoryType, Pageable pageable, Long teamId);
}
