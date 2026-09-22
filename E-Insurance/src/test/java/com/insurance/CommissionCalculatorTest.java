package com.insurance;

import com.insurance.dto.AgentCommissionSummaryDTO;
import com.insurance.dto.CommissionCalculationRequestDTO;
import com.insurance.exception.ResourceNotFoundException;
import com.insurance.model.Customer;
import com.insurance.model.InsuranceAgent;
import com.insurance.model.InsurancePlan;
import com.insurance.model.Policy;
import com.insurance.model.Scheme;
import com.insurance.repository.CommissionRepository;
import com.insurance.repository.InsuranceAgentRepository;
import com.insurance.repository.PolicyRepository;
import com.insurance.service.CommissionCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommissionCalculatorTest {

    @Mock
    private InsuranceAgentRepository agentRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private CommissionRepository commissionRepository;

    @InjectMocks
    private CommissionCalculatorService commissionCalculatorService;

    private InsuranceAgent sampleAgent;
    private Policy samplePolicy1;
    private Policy samplePolicy2;

    @BeforeEach
    void setUp() {
        sampleAgent = InsuranceAgent.builder()
                .agentId(1)
                .username("agent_rahul")
                .fullName("Rahul Sharma")
                .email("rahul@insurance.com")
                .createdAt(LocalDateTime.now())
                .build();

        Customer customer1 = Customer.builder()
                .customerId(10)
                .fullName("Priya Singh")
                .email("priya@example.com")
                .agent(sampleAgent)
                .build();

        InsurancePlan plan = InsurancePlan.builder().planId(1).planName("Life Guard").build();
        Scheme scheme = Scheme.builder().schemeId(20).schemeName("Smart Life 2026").plan(plan).build();

        samplePolicy1 = Policy.builder()
                .policyId(101)
                .customer(customer1)
                .scheme(scheme)
                .policyDetails("Standard Life Cover")
                .premium(new BigDecimal("10000.00"))
                .dateIssued(LocalDate.now())
                .maturityPeriod(60)
                .policyLapseDate(LocalDate.now().plusMonths(60))
                .createdAt(LocalDateTime.now())
                .build();

        samplePolicy2 = Policy.builder()
                .policyId(102)
                .customer(customer1)
                .scheme(scheme)
                .policyDetails("Accident Rider Cover")
                .premium(new BigDecimal("20000.00"))
                .dateIssued(LocalDate.now())
                .maturityPeriod(120)
                .policyLapseDate(LocalDate.now().plusMonths(120))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCalculateAgentCommission_DefaultRate() {
        when(agentRepository.findById(1)).thenReturn(Optional.of(sampleAgent));
        when(policyRepository.findByCustomerAgentAgentId(1)).thenReturn(List.of(samplePolicy1, samplePolicy2));
        when(commissionRepository.findByPolicyPolicyId(anyInt())).thenReturn(Collections.emptyList());

        AgentCommissionSummaryDTO summary = commissionCalculatorService.calculateAgentCommission(1, null);

        assertNotNull(summary);
        assertEquals(1, summary.getAgentId());
        assertEquals("Rahul Sharma", summary.getAgentFullName());
        assertEquals(2, summary.getTotalPoliciesSold());
        assertEquals(new BigDecimal("30000.00"), summary.getTotalPremiumAmount());
        assertEquals(new BigDecimal("5.00"), summary.getCommissionRate()); // Default 5%
        // 5% of 30,000 = 1,500
        assertEquals(new BigDecimal("1500.00"), summary.getTotalCommissionAmount());
        assertEquals(2, summary.getPolicies().size());
        assertEquals(new BigDecimal("500.00"), summary.getPolicies().get(0).getCommissionAmount());
        assertEquals(new BigDecimal("1000.00"), summary.getPolicies().get(1).getCommissionAmount());
    }

    @Test
    void testCalculateAgentCommission_CustomRate() {
        when(agentRepository.findById(1)).thenReturn(Optional.of(sampleAgent));
        when(policyRepository.findByCustomerAgentAgentId(1)).thenReturn(List.of(samplePolicy1));

        CommissionCalculationRequestDTO request = CommissionCalculationRequestDTO.builder()
                .agentId(1)
                .commissionRate(new BigDecimal("10.00"))
                .build();

        AgentCommissionSummaryDTO summary = commissionCalculatorService.calculateCommissionWithRequest(request);

        assertNotNull(summary);
        assertEquals(1, summary.getTotalPoliciesSold());
        assertEquals(new BigDecimal("10000.00"), summary.getTotalPremiumAmount());
        assertEquals(new BigDecimal("10.00"), summary.getCommissionRate());
        // 10% of 10,000 = 1,000
        assertEquals(new BigDecimal("1000.00"), summary.getTotalCommissionAmount());
    }

    @Test
    void testCalculateAgentCommission_ZeroPoliciesSold() {
        when(agentRepository.findById(1)).thenReturn(Optional.of(sampleAgent));
        when(policyRepository.findByCustomerAgentAgentId(1)).thenReturn(Collections.emptyList());

        AgentCommissionSummaryDTO summary = commissionCalculatorService.calculateAgentCommission(1, null);

        assertNotNull(summary);
        assertEquals(0, summary.getTotalPoliciesSold());
        assertEquals(new BigDecimal("0.00"), summary.getTotalPremiumAmount());
        assertEquals(new BigDecimal("0.00"), summary.getTotalCommissionAmount());
        assertTrue(summary.getPolicies().isEmpty());
    }

    @Test
    void testCalculateAgentCommission_AgentNotFound() {
        when(agentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commissionCalculatorService.calculateAgentCommission(999, null));
    }
}
