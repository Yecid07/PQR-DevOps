package com.devops.api.pqr.reviewer;

import com.devops.api.pqr.reviewer.entity.Reviewer;
import com.devops.api.pqr.reviewer.dto.CreateReviewerDto;
import com.devops.api.pqr.reviewer.mapper.ReviewerMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviewer")
public class ReviewerController {
    private final ReviewerMapper reviewerMapper;
    private final ReviewerService reviewerService;

    public ReviewerController(ReviewerService reviewerService, ReviewerMapper reviewerMapper) {
        this.reviewerService = reviewerService;
        this.reviewerMapper = reviewerMapper;
    }

    @PostMapping
    public ResponseEntity<Reviewer> save(@Valid @RequestBody CreateReviewerDto createReviewerDto) {
        Reviewer reviewer = reviewerMapper.toReviewer(createReviewerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewerService.saveReviewer(reviewer));
    }

}
