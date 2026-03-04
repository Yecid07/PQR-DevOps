package com.devops.api.pqr.reviewer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewerDto {
    @NotBlank()
    private String name;
    @NotBlank()
    @Email()
    private String email;
}
