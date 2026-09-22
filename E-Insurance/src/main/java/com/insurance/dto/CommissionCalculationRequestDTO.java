package com.insurance.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
public class CommissionCalculationRequestDTO {

    @NotNull(message = "Agent ID is required")
    private Integer agentId;

    /**
     * Optional custom commission rate percentage (e.g. 5.0 for 5.0%, 10.0 for 10.0%).
     * If null, the default rate of 5.0% will be applied.
     */
    @DecimalMin(value = "0.00", message = "Commission rate cannot be negative")
    @DecimalMax(value = "100.00", message = "Commission rate cannot exceed 100%")
    private BigDecimal commissionRate;
}
