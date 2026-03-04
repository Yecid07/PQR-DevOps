package com.devops.api.pqr.reviewer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewerController.class)
@DisplayName("Reviewer Controller Tests")
class ReviewerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewerService reviewerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL = "/api/v1/reviewer";

    @Test
    @DisplayName("POST /reviewer - Should create a new reviewer successfully")
    void testSaveShouldReturnCreatedWhenSavingReviewer() throws Exception {
        // GIVEN
        Reviewer input = createReviewerRequest();
        Reviewer expected = createReviewerResponse();

        given(reviewerService.saveReviewer(any(Reviewer.class))).willReturn(expected);

        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.email").value(expected.getEmail()));
    }

    private Reviewer createReviewerRequest() {
        return Reviewer.builder()
                .name("pedro")
                .email("pedro@gmail.com")
                .build();
    }

    private Reviewer createReviewerResponse() {
        return Reviewer.builder()
                .id("123")
                .name("pedro")
                .email("pedro@gmail.com")
                .build();
    }
}