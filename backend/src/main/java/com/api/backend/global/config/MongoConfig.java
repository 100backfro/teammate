package com.api.backend.global.config;

import com.api.backend.comment.data.repository.CommentRepository;
import com.api.backend.documents.data.repository.DocumentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@RequiredArgsConstructor
@EnableMongoRepositories(basePackageClasses = {DocumentsRepository.class, CommentRepository.class})
public class MongoConfig {

}
