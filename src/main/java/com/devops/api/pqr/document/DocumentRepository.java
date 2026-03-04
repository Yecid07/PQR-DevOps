package com.devops.api.pqr.document;


import com.devops.api.pqr.document.entity.Document;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document, String> {
}
