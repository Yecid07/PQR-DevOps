package com.devops.api.pqr.pqr.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePqrDto {
    @NotBlank
    @Pattern(regexp = "peticion|queja|reclamo")
    private String type;

    @Email
    @NotBlank
    private String customerEmail;

    @NotBlank
    private String description;
}