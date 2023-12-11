package com.api.backend.category.controller;

import com.api.backend.category.data.dto.ScheduleCategoryDto;
import com.api.backend.category.data.dto.ScheduleCategoryRequest;
import com.api.backend.category.service.ScheduleCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;


@MockBean(JpaMetamodelMappingContext.class)
class ScheduleCategoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ScheduleCategoryService scheduleCategoryService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("카테고리 추가 성공")
  void categoryAddSuccess() throws Exception {
    // Mock 데이터 설정
    ScheduleCategoryRequest request = ScheduleCategoryRequest.builder()
        .color("Red")
        .categoryName("Test Category")
        .build();

    ScheduleCategoryDto mockResponse = ScheduleCategoryDto.builder()
        .categoryId(1L)
        .createDt(LocalDateTime.now())
        .color("Red")
        .categoryName("Test Category")
        .build();
//TODO: api 완성 후 테스트 코드 구현 예정

//    when(scheduleCategoryService.add(any(ScheduleCategoryRequest.class), any(Long.class)))
//        .thenReturn(mockResponse);
//
//    mockMvc.perform(MockMvcRequestBuilders.post("/category")
//            .contentType(MediaType.APPLICATION_JSON)
//            .param("teamId", "1")
//            .content(objectMapper.writeValueAsString(request)))
//        .andExpect(MockMvcResultMatchers.status().isOk())
//        .andExpect(MockMvcResultMatchers.jsonPath("$.categoryName").value("Test Category"))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.color").value("Red"));
  }
}