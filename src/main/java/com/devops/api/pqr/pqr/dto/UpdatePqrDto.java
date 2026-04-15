package com.devops.api.pqr.pqr.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdatePqrDto {

    @Pattern(regexp = "peticion|queja|reclamo")
    private String type;

    @Email
    private String customerEmail;

    private String description;
    private String subject;

    @Valid
    private BookDto book;
}