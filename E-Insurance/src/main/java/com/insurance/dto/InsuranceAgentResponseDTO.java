package com.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceAgentResponseDTO {
    private Integer agentId;
    private String username;
    private String email;
    private String fullName;
    private LocalDateTime createdAt;
}
