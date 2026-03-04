package com.devops.api.pqr.reviewer;

import com.devops.api.pqr.reviewer.dto.CreateReviewerDto;
import com.devops.api.pqr.reviewer.entity.Reviewer;
import com.devops.api.pqr.reviewer.mapper.ReviewerMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewerController.class)
@DisplayName("Reviewer Controller Tests")
class ReviewerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewerService reviewerService;

    @MockitoBean
    private ReviewerMapper reviewerMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL = "/api/v1/reviewer";

    @Test
    @DisplayName("POST /reviewer - Should create a new reviewer successfully")
    void testSaveShouldReturnCreatedWhenSavingReviewer() throws Exception {
        // GIVEN
        CreateReviewerDto dto = new CreateReviewerDto("pedro", "pedro@gmail.com");
        Reviewer expected = createReviewerResponse();
        given(reviewerMapper.toReviewer(any(CreateReviewerDto.class))).willReturn(createReviewerRequest());
        given(reviewerService.saveReviewer(any(Reviewer.class))).willReturn(expected);

        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.email").value(expected.getEmail()));
    }

    @Test
    @DisplayName("POST /reviewer - Should return 400 when email is invalid")
    void testSaveShouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        // GIVEN
        CreateReviewerDto invalidDto = new CreateReviewerDto("pedro", "email-invalido");

        // WHEN
        // THEN
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

    }
    @Test
    @DisplayName("DELETE /reviewer/{id} - Should return 200 when deleted successfully")
    void testDeleteShouldReturnOkWhenReviewerExists() throws Exception {
        // GIVEN
        String id = "123";
        // WHEN
        // THEN
        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string("Reviewer deleted correctly"));
    }

    @Test
    @DisplayName("DELETE /reviewer/{id} - Should return 400 when service throws exception")
    void testDeleteShouldReturnBadRequestWhenIdNotFound() throws Exception {
        // GIVEN
        String id = "invalid-id";
        willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id not found"))
                .given(reviewerService).delete(id);

        // WHEN
        // THEN
        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isBadRequest());
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