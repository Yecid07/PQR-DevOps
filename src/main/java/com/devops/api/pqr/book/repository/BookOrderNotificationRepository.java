package com.devops.api.pqr.book.repository;

import com.devops.api.pqr.book.entity.BookOrderNotification;
import org.springframework.data.repository.CrudRepository;

public interface BookOrderNotificationRepository extends CrudRepository<BookOrderNotification, String> {
    boolean existsByBookTitleAndBookAuthor(String bookTitle, String bookAuthor);
}
