package com.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private Integer paymentId;
    private Integer customerId;
    private String customerName;
    private Integer policyId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private LocalDateTime createdAt;
}
