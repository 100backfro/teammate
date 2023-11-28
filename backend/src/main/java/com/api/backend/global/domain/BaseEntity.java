package com.api.backend.global.domain;


import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @Column(nullable = false,updatable = false)
  @CreatedDate
  private LocalDateTime createDt;

  @Column(nullable = false)
  @LastModifiedDate
  private LocalDateTime updateDt;
}
