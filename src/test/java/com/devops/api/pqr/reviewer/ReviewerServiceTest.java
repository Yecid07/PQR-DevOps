package com.devops.api.pqr.reviewer;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import com.devops.api.pqr.reviewer.entity.Reviewer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ReviewerServiceTest {

    @Mock
    private ReviewerRepository reviewerRepository;

    @InjectMocks
    private ReviewerService reviewerService;

    private Reviewer sampleInput;
    private Reviewer sampleOutput;

    @BeforeEach
    void setUp() {
        sampleInput = Reviewer.builder()
                .name("pedro")
                .email("pedro@gmail.com")
                .build();

        sampleOutput = Reviewer.builder()
                .id(UUID.randomUUID().toString())
                .name("pedro")
                .email("pedro@gmail.com")
                .build();
    }

    @Test
    @DisplayName("Should save a reviewer successfully")
    void testSaveShouldSaveReviewerSuccessfulWhenGivenValidUser() {
        //GIVEN
        Reviewer expected = sampleOutput;
        given(reviewerRepository.save(sampleInput)).willReturn(expected);

        //WHEN
        Reviewer actual = reviewerService.saveReviewer(sampleInput);

        //THEN
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should delete a reviewer successfully when ID exists")
    void testDeleteShouldSucceedWhenIdExists() {
        // GIVEN
        String id = sampleOutput.getId();
        given(reviewerRepository.findById(id)).willReturn(Optional.of(sampleOutput));

        // WHEN
        reviewerService.delete(id);

        // THEN
        verify(reviewerRepository).delete(sampleOutput);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when ID does not exist")
    void testDeleteShouldThrowExceptionWhenIdDoesNotExist() {
        // GIVEN
        String id = "non-existent-id";
        given(reviewerRepository.findById(id)).willReturn(Optional.empty());

        // WHEN
        // THEN
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reviewerService.delete(id);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Id not found", exception.getReason());
    }
}