package com.devops.api.pqr.reviewer;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

import com.devops.api.pqr.reviewer.entity.Reviewer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}