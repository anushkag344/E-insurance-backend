package com.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {
    private Integer customerId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Integer agentId;
    private String agentName;
    private LocalDateTime createdAt;
}
