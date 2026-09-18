package com.insurance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemeRequestDTO {

    @NotBlank(message = "Scheme name is required")
    private String schemeName;

    @NotBlank(message = "Scheme details are required")
    private String schemeDetails;

    @NotNull(message = "Plan ID is required")
    private Integer planId;
}
