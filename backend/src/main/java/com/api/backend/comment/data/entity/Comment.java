package com.api.backend.comment.data.entity;

import java.time.LocalDateTime;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@Document(collection = "Comment")
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
  @Id
  private String id;

  @Field(name = "writerId")
  private Long writerId;

  @Field(name = "content")
  private String content;

  @Field(name = "team_id")
  private Long teamId;

  @CreatedDate
  @Field(name = "created_dt")
  private LocalDateTime createdDt;

  @LastModifiedDate
  @Field(name = "updated_dt")
  private LocalDateTime updatedDt;
}
