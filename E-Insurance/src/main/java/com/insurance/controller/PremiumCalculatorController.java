package com.insurance.controller;

import com.insurance.dto.ApiResponse;
import com.insurance.dto.PremiumCalculateRequestDTO;
import com.insurance.dto.PremiumCalculateResponseDTO;
import com.insurance.service.PremiumCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/calculator")
@Tag(name = "Premium Calculator (Use Case 5)", description = "Endpoints for Customer and Insurance Agent to calculate insurance premiums based on age, term, ROI, and payment frequency.")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('CUSTOMER', 'AGENT', 'ADMIN', 'EMPLOYEE')")
public class PremiumCalculatorController {

    private final PremiumCalculatorService premiumCalculatorService;

    public PremiumCalculatorController(PremiumCalculatorService premiumCalculatorService) {
        this.premiumCalculatorService = premiumCalculatorService;
    }

    @PostMapping("/premium")
    @Operation(summary = "UC5: Calculate Insurance Premium", description = "Calculate premium and estimated maturity benefit based on age, sum assured, term years, payment frequency, and rate of interest.")
    public ResponseEntity<ApiResponse<PremiumCalculateResponseDTO>> calculatePremium(
            @Valid @RequestBody PremiumCalculateRequestDTO request) {
        PremiumCalculateResponseDTO response = premiumCalculatorService.calculatePremium(request);
        return ResponseEntity.ok(ApiResponse.ok("Premium calculated successfully", response));
    }
}
