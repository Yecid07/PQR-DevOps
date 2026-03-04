package com.devops.api.pqr.reviewer;

import com.devops.api.pqr.reviewer.entity.Reviewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewerService {
    @Autowired
    private ReviewerRepository reviewerRepository;

    public Reviewer saveReviewer(Reviewer reviewer){
        return reviewerRepository.save(reviewer);
    }

}
