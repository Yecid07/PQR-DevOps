package com.devops.api.pqr.reviewer;

import com.devops.api.pqr.reviewer.entity.Reviewer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewerRepository extends CrudRepository<Reviewer, String> {}
