package com.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsurancePlanDTO {
    private Integer planId;
    private String planName;
    private String planDetails;
    private LocalDateTime createdAt;
    private List<SchemeDTO> schemes;
}
