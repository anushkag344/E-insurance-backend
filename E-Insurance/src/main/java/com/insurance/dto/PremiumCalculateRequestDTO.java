package com.insurance.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumCalculateRequestDTO {

    private Integer schemeId;

    @NotNull(message = "Sum assured / coverage amount is required")
    @DecimalMin(value = "1000.00", message = "Minimum sum assured is 1,000")
    private BigDecimal sumAssured;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Minimum eligible age is 18 years")
    @Max(value = 75, message = "Maximum eligible age is 75 years")
    private Integer age;

    @NotNull(message = "Term in years is required")
    @Min(value = 1, message = "Minimum policy term is 1 year")
    @Max(value = 50, message = "Maximum policy term is 50 years")
    private Integer termYears;

    /**
     * Payment frequency: ANNUAL, HALF_YEARLY, QUARTERLY, MONTHLY. Default: ANNUAL
     */
    @Builder.Default
    private String paymentFrequency = "ANNUAL";

    /**
     * Optional annual rate of interest (percentage, e.g. 6.5 for 6.5%). Default: 6.0%
     */
    @DecimalMin(value = "0.00", message = "Rate of interest cannot be negative")
    @DecimalMax(value = "30.00", message = "Rate of interest cannot exceed 30%")
    private BigDecimal rateOfInterest;
}
