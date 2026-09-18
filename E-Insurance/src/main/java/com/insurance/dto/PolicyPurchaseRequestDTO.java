package com.insurance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyPurchaseRequestDTO {

    @NotNull(message = "Scheme ID is required")
    private Integer schemeId;

    @NotBlank(message = "Policy details are required")
    private String policyDetails;

    @NotNull(message = "Premium amount is required")
    @DecimalMin(value = "0.01", message = "Premium must be greater than 0")
    private BigDecimal premium;

    @NotNull(message = "Maturity period (in months) is required")
    @Min(value = 1, message = "Maturity period must be at least 1 month")
    private Integer maturityPeriod;

    @NotNull(message = "Initial payment amount is required")
    @DecimalMin(value = "0.01", message = "Initial payment must be greater than 0")
    private BigDecimal initialPaymentAmount;
}
