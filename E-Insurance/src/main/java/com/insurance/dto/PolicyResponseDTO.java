package com.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyResponseDTO {
    private Integer policyId;
    private Integer customerId;
    private String customerName;
    private Integer schemeId;
    private String schemeName;
    private String planName;
    private String policyDetails;
    private BigDecimal premium;
    private LocalDate dateIssued;
    private Integer maturityPeriod;
    private LocalDate policyLapseDate;
    private LocalDateTime createdAt;
    private List<PaymentResponseDTO> payments;
}
