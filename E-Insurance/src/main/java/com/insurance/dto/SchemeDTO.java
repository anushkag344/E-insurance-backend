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
public class SchemeDTO {
    private Integer schemeId;
    private String schemeName;
    private String schemeDetails;
    private Integer planId;
    private String planName;
    private LocalDateTime createdAt;
}
