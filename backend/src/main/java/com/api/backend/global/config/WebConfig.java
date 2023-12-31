package com.api.backend.global.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${frontend.host}")
  String host;
  @Value("${frontend.port}")
  String port;
  @Value("${frontend.prod}")
  String domain;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver
        = new SortHandlerMethodArgumentResolver();
    sortHandlerMethodArgumentResolver.setSortParameter("sortBy");
    sortHandlerMethodArgumentResolver.setPropertyDelimiter("-");

    PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver
        = new PageableHandlerMethodArgumentResolver(sortHandlerMethodArgumentResolver);
    pageableHandlerMethodArgumentResolver.setOneIndexedParameters(true);
    pageableHandlerMethodArgumentResolver.setMaxPageSize(500);
    pageableHandlerMethodArgumentResolver.setFallbackPageable(PageRequest.of(0, 10));
    argumentResolvers.add(pageableHandlerMethodArgumentResolver);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String[] origins = {"http://" + host + ":" + port, domain};

    registry.addMapping("/**")
        .allowedOrigins(origins)
        .allowedOriginPatterns("*");
  }
}
