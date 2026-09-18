package com.insurance.service;

import com.insurance.dto.PaymentResponseDTO;
import com.insurance.dto.PolicyPurchaseRequestDTO;
import com.insurance.dto.PolicyResponseDTO;
import com.insurance.exception.ResourceNotFoundException;
import com.insurance.model.*;
import com.insurance.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final SchemeRepository schemeRepository;
    private final CommissionRepository commissionRepository;

    public PolicyService(
            PolicyRepository policyRepository,
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository,
            SchemeRepository schemeRepository,
            CommissionRepository commissionRepository) {
        this.policyRepository = policyRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.schemeRepository = schemeRepository;
        this.commissionRepository = commissionRepository;
    }

    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> getMyPolicies(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for username: " + username));

        return policyRepository.findByCustomerCustomerId(customer.getCustomerId()).stream()
                .map(this::mapToPolicyResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getMyPayments(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for username: " + username));

        return paymentRepository.findByCustomerCustomerId(customer.getCustomerId()).stream()
                .map(this::mapToPaymentResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> getPoliciesByCustomerId(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        return policyRepository.findByCustomerCustomerId(customerId).stream()
                .map(this::mapToPolicyResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByCustomerId(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        return paymentRepository.findByCustomerCustomerId(customerId).stream()
                .map(this::mapToPaymentResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PolicyResponseDTO purchasePolicy(String username, PolicyPurchaseRequestDTO request) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for username: " + username));

        Scheme scheme = schemeRepository.findById(request.getSchemeId())
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with id: " + request.getSchemeId()));

        LocalDate issuedDate = LocalDate.now();
        LocalDate lapseDate = issuedDate.plusMonths(request.getMaturityPeriod());

        Policy policy = Policy.builder()
                .customer(customer)
                .scheme(scheme)
                .policyDetails(request.getPolicyDetails())
                .premium(request.getPremium())
                .dateIssued(issuedDate)
                .maturityPeriod(request.getMaturityPeriod())
                .policyLapseDate(lapseDate)
                .build();

        Policy savedPolicy = policyRepository.save(policy);

        Payment payment = Payment.builder()
                .customer(customer)
                .policy(savedPolicy)
                .amount(request.getInitialPaymentAmount())
                .paymentDate(issuedDate)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        savedPolicy.getPayments().add(savedPayment);

        if (customer.getAgent() != null) {
            BigDecimal commissionRate = new BigDecimal("0.05"); // 5% commission
            BigDecimal commissionAmount = request.getInitialPaymentAmount()
                    .multiply(commissionRate)
                    .setScale(2, RoundingMode.HALF_UP);

            Commission commission = Commission.builder()
                    .agent(customer.getAgent())
                    .policy(savedPolicy)
                    .commissionAmount(commissionAmount)
                    .build();

            commissionRepository.save(commission);
        }

        return mapToPolicyResponseDTO(savedPolicy);
    }

    public PolicyResponseDTO mapToPolicyResponseDTO(Policy policy) {
        List<PaymentResponseDTO> payments = policy.getPayments() != null
                ? policy.getPayments().stream().map(this::mapToPaymentResponseDTO).collect(Collectors.toList())
                : List.of();

        String planName = (policy.getScheme() != null && policy.getScheme().getPlan() != null)
                ? policy.getScheme().getPlan().getPlanName()
                : null;

        return PolicyResponseDTO.builder()
                .policyId(policy.getPolicyId())
                .customerId(policy.getCustomer() != null ? policy.getCustomer().getCustomerId() : null)
                .customerName(policy.getCustomer() != null ? policy.getCustomer().getFullName() : null)
                .schemeId(policy.getScheme() != null ? policy.getScheme().getSchemeId() : null)
                .schemeName(policy.getScheme() != null ? policy.getScheme().getSchemeName() : null)
                .planName(planName)
                .policyDetails(policy.getPolicyDetails())
                .premium(policy.getPremium())
                .dateIssued(policy.getDateIssued())
                .maturityPeriod(policy.getMaturityPeriod())
                .policyLapseDate(policy.getPolicyLapseDate())
                .createdAt(policy.getCreatedAt())
                .payments(payments)
                .build();
    }

    public PaymentResponseDTO mapToPaymentResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .customerId(payment.getCustomer() != null ? payment.getCustomer().getCustomerId() : null)
                .customerName(payment.getCustomer() != null ? payment.getCustomer().getFullName() : null)
                .policyId(payment.getPolicy() != null ? payment.getPolicy().getPolicyId() : null)
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
