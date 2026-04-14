package com.devops.api.pqr.pqr;

import com.devops.api.pqr.pqr.entity.Pqr;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PqrRepository extends CrudRepository<Pqr, String> {
    @Query(value = "SELECT COUNT(p) FROM pqr p " +
            "WHERE p.type = :type " +
            "AND p.subject = :subject " +
            "AND p.book->>'bookTitle' = :bookTitle " +
            "AND p.book->>'bookAuthor' = :bookAuthor",
            nativeQuery = true)
    long countByTypeSubjectAndBook(
            @Param("type") String type,
            @Param("subject") String subject,
            @Param("bookTitle") String bookTitle,
            @Param("bookAuthor") String bookAuthor);
}
