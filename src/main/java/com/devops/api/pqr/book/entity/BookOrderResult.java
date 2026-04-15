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
public class BookOrderResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String externalPqrId;
    private String asunto;
    private String responsable;
    private int conteo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "book_id"))})
    private BookInfo book;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "receipt_id"))
    })
    private ReceiptInfo receipt;

    private String bookTitle;
    private String bookAuthor;
    private LocalDateTime savedAt;
}