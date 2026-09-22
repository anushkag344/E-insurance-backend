package com.insurance.service;

import com.insurance.dto.AgentCommissionSummaryDTO;
import com.insurance.dto.AgentPolicyCommissionDTO;
import com.insurance.dto.CommissionCalculationRequestDTO;
import com.insurance.exception.ResourceNotFoundException;
import com.insurance.model.Commission;
import com.insurance.model.InsuranceAgent;
import com.insurance.model.Policy;
import com.insurance.repository.CommissionRepository;
import com.insurance.repository.InsuranceAgentRepository;
import com.insurance.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommissionCalculatorService {

    private final InsuranceAgentRepository agentRepository;
    private final PolicyRepository policyRepository;
    private final CommissionRepository commissionRepository;

    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("5.00");

    public CommissionCalculatorService(
            InsuranceAgentRepository agentRepository,
            PolicyRepository policyRepository,
            CommissionRepository commissionRepository) {
        this.agentRepository = agentRepository;
        this.policyRepository = policyRepository;
        this.commissionRepository = commissionRepository;
    }

    @Transactional(readOnly = true)
    public AgentCommissionSummaryDTO calculateAgentCommission(Integer agentId, BigDecimal customRate) {
        InsuranceAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance Agent not found with ID: " + agentId));

        BigDecimal appliedRate = (customRate != null && customRate.compareTo(BigDecimal.ZERO) >= 0)
                ? customRate.setScale(2, RoundingMode.HALF_UP)
                : DEFAULT_COMMISSION_RATE;

        List<Policy> policies = policyRepository.findByCustomerAgentAgentId(agentId);

        List<AgentPolicyCommissionDTO> policyDTOs = new ArrayList<>();
        BigDecimal totalPremium = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;

        for (Policy policy : policies) {
            BigDecimal policyPremium = policy.getPremium() != null ? policy.getPremium() : BigDecimal.ZERO;
            totalPremium = totalPremium.add(policyPremium);

            BigDecimal policyCommission;
            if (customRate != null) {
                // If custom rate is provided for simulation / calculation, apply custom rate directly to policy premium
                policyCommission = policyPremium.multiply(appliedRate)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                // Check if an existing commission record was recorded for this policy and agent
                List<Commission> recordedCommissions = commissionRepository.findByPolicyPolicyId(policy.getPolicyId());
                if (!recordedCommissions.isEmpty() && recordedCommissions.get(0).getCommissionAmount() != null) {
                    policyCommission = recordedCommissions.get(0).getCommissionAmount();
                } else {
                    policyCommission = policyPremium.multiply(appliedRate)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                }
            }

            totalCommission = totalCommission.add(policyCommission);

            String customerName = policy.getCustomer() != null ? policy.getCustomer().getFullName() : null;
            String customerEmail = policy.getCustomer() != null ? policy.getCustomer().getEmail() : null;
            Integer customerId = policy.getCustomer() != null ? policy.getCustomer().getCustomerId() : null;

            Integer schemeId = policy.getScheme() != null ? policy.getScheme().getSchemeId() : null;
            String schemeName = policy.getScheme() != null ? policy.getScheme().getSchemeName() : null;
            String planName = (policy.getScheme() != null && policy.getScheme().getPlan() != null)
                    ? policy.getScheme().getPlan().getPlanName()
                    : null;

            policyDTOs.add(AgentPolicyCommissionDTO.builder()
                    .policyId(policy.getPolicyId())
                    .customerId(customerId)
                    .customerName(customerName)
                    .customerEmail(customerEmail)
                    .schemeId(schemeId)
                    .schemeName(schemeName)
                    .planName(planName)
                    .policyDetails(policy.getPolicyDetails())
                    .premium(policyPremium.setScale(2, RoundingMode.HALF_UP))
                    .dateIssued(policy.getDateIssued())
                    .maturityPeriod(policy.getMaturityPeriod())
                    .policyLapseDate(policy.getPolicyLapseDate())
                    .commissionRate(appliedRate)
                    .commissionAmount(policyCommission.setScale(2, RoundingMode.HALF_UP))
                    .createdAt(policy.getCreatedAt())
                    .build());
        }

        return AgentCommissionSummaryDTO.builder()
                .agentId(agent.getAgentId())
                .agentUsername(agent.getUsername())
                .agentFullName(agent.getFullName())
                .agentEmail(agent.getEmail())
                .totalPoliciesSold(policies.size())
                .totalPremiumAmount(totalPremium.setScale(2, RoundingMode.HALF_UP))
                .commissionRate(appliedRate)
                .totalCommissionAmount(totalCommission.setScale(2, RoundingMode.HALF_UP))
                .policies(policyDTOs)
                .build();
    }

    @Transactional(readOnly = true)
    public AgentCommissionSummaryDTO calculateCommissionWithRequest(CommissionCalculationRequestDTO request) {
        return calculateAgentCommission(request.getAgentId(), request.getCommissionRate());
    }

    @Transactional(readOnly = true)
    public List<AgentCommissionSummaryDTO> getAllAgentsCommissionSummary() {
        List<InsuranceAgent> agents = agentRepository.findAll();
        return agents.stream()
                .map(agent -> calculateAgentCommission(agent.getAgentId(), null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgentCommissionSummaryDTO getAgentCommissionByUsername(String username) {
        InsuranceAgent agent = agentRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with username: " + username));
        return calculateAgentCommission(agent.getAgentId(), null);
    }
}
