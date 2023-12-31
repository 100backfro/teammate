package com.api.backend.global.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.api.backend.file.util.ProcessUtil;
import io.findify.s3mock.S3Mock;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class EmbeddedS3Config {

  @Value("${embedded.aws.s3.mock.port}")
  private int port;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final S3Mock s3Mock;

  @PostConstruct
  public void startS3Mock() throws IOException {
    port = ProcessUtil.isRunningPort(port) ? ProcessUtil.findAvailableRandomProt() : port;
    s3Mock.start();
    log.info("인메모리 S3 Mock 서버가 시작됩니다. port: {}", port);
  }

  @PreDestroy
  public void destroyS3Mock() {
    s3Mock.shutdown();
    log.info("인메모리 S3 Mock 서버가 종료됩니다. port: {}", port);
  }

  @Bean
  @Primary
  public AmazonS3 amazonS3Client() {
    AwsClientBuilder.EndpointConfiguration endpoint = new EndpointConfiguration(getUri(), "ap-northeast-2");
    AmazonS3 client = AmazonS3ClientBuilder
        .standard()
        .withPathStyleAccessEnabled(true)
        .withEndpointConfiguration(endpoint)
        .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
        .build();
    client.createBucket(bucket);
    return client;
  }

  private String getUri() {
    return UriComponentsBuilder.newInstance()
        .scheme("http")
        .host("localhost")
        .port(port)
        .build()
        .toUriString();
  }
}
