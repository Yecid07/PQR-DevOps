package com.devops.api.pqr.reviewer;

import com.devops.api.pqr.reviewer.entity.Reviewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Iterator;
import java.util.Optional;

@Service
public class ReviewerService {
    @Autowired
    private ReviewerRepository reviewerRepository;

    public Reviewer saveReviewer(Reviewer reviewer) {
        return reviewerRepository.save(reviewer);
    }

    public void delete(String id) {
        Reviewer reviewer = reviewerRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id not found"));
        reviewerRepository.delete(reviewer);
    }

    public Iterable<Reviewer> getAll(){
        return reviewerRepository.findAll();
    }

}
