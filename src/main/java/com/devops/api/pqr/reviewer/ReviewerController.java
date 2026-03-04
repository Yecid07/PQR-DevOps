package com.devops.api.pqr.reviewer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviewer") // This defines the base path
public class ReviewerController {
    private final ReviewerService reviewerService;

    public ReviewerController(ReviewerService reviewerService) {
        this.reviewerService = reviewerService;
    }

    @PostMapping
    public ResponseEntity<Reviewer> save(@RequestBody Reviewer data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewerService.saveReviewer(data));
    }

}
