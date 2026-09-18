package com.insurance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsurancePlanRequestDTO {

    @NotBlank(message = "Plan name is required")
    private String planName;

    @NotBlank(message = "Plan details are required")
    private String planDetails;
}
