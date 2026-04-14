package com.devops.api.pqr.pqr.dto;

import jakarta.validation.constraints.NotBlank;

public record BookDto(
        @NotBlank String bookTitle,
        @NotBlank String bookAuthor) {
}