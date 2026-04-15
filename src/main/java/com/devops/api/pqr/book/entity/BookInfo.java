package com.devops.api.pqr.book.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class BookInfo {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private boolean available;
}