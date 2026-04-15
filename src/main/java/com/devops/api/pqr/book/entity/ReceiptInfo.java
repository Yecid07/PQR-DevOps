package com.devops.api.pqr.book.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ReceiptInfo {
    private String id;
    private String empresa;
    private String nit;
    private String item;
    private double valor;
    private String fecha;
    private String pdfUrl;
}