package com.devops.api.pqr.book.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookOrderNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String bookTitle;
    private String bookAuthor;
    private LocalDateTime notifiedAt;
}