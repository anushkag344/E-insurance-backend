package com.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumCalculateResponseDTO {

    private Integer schemeId;
    private String schemeName;
    private String planName;

    private BigDecimal sumAssured;
    private Integer age;
    private Integer termYears;
    private String paymentFrequency;
    private BigDecimal rateOfInterest;

    private BigDecimal annualPremium;
    private BigDecimal installmentPremium;
    private Integer totalInstallments;
    private BigDecimal totalPremiumPayable;

    private BigDecimal estimatedMaturityAmount;
    private BigDecimal totalBenefit;

    private String calculationSummary;
}
