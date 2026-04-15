package com.devops.api.pqr.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BookOrderResponse {

    @JsonProperty("pqr")
    private PqrResponse pqr;

    @JsonProperty("libro")
    private BookResponse book;

    @JsonProperty("receipt")
    private ReceiptResponse receipt;

    @JsonProperty("pdf_url")
    private String pdfUrl;

    @Data
    public static class PqrResponse {
        private String id;
        private String asunto;
        private String responsable;
        private int conteo;
    }

    @Data
    public static class BookResponse {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private boolean available;
        private String createdAt;
    }

    @Data
    public static class ReceiptResponse {
        private String id;
        private String empresa;
        private String nit;
        private String item;
        private double valor;
        private String fecha;
        @JsonProperty("pdf_url")
        private String pdfUrl;
    }
}