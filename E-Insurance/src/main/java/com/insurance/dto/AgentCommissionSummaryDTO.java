package com.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentCommissionSummaryDTO {

    private Integer agentId;
    private String agentUsername;
    private String agentFullName;
    private String agentEmail;

    private Integer totalPoliciesSold;
    private BigDecimal totalPremiumAmount;
    private BigDecimal commissionRate;
    private BigDecimal totalCommissionAmount;

    @Builder.Default
    private List<AgentPolicyCommissionDTO> policies = new ArrayList<>();
}
