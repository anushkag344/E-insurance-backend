package com.insurance.controller;

import com.insurance.dto.AgentCommissionSummaryDTO;
import com.insurance.dto.ApiResponse;
import com.insurance.dto.CommissionCalculationRequestDTO;
import com.insurance.service.CommissionCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admin/commission-calculator")
@Tag(name = "Commission Calculator (Use Case 6)", description = "Admin endpoints to calculate commission for insurance agents based on policies sold.")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class CommissionCalculatorController {

    private final CommissionCalculatorService commissionCalculatorService;

    public CommissionCalculatorController(CommissionCalculatorService commissionCalculatorService) {
        this.commissionCalculatorService = commissionCalculatorService;
    }

    @GetMapping("/agents")
    @Operation(summary = "UC6 Step 2: List Agents for Commission Calculator", description = "Admin lists all insurance agents and their total policies sold and commission earned.")
    public ResponseEntity<ApiResponse<List<AgentCommissionSummaryDTO>>> getAllAgentsCommissionSummary() {
        List<AgentCommissionSummaryDTO> list = commissionCalculatorService.getAllAgentsCommissionSummary();
        return ResponseEntity.ok(ApiResponse.ok("Agents commission summary retrieved successfully", list));
    }

    @GetMapping("/agents/{agentId}")
    @Operation(summary = "UC6 Step 3-5: Calculate Agent Commission", description = "Admin selects an agent to retrieve all policies sold and calculate commission.")
    public ResponseEntity<ApiResponse<AgentCommissionSummaryDTO>> getAgentCommission(@PathVariable Integer agentId) {
        AgentCommissionSummaryDTO summary = commissionCalculatorService.calculateAgentCommission(agentId, null);
        return ResponseEntity.ok(ApiResponse.ok("Agent commission calculated successfully", summary));
    }

    @PostMapping("/calculate")
    @Operation(summary = "UC6: Simulate / Calculate Commission with Custom Rate", description = "Admin calculates agent commission using a custom commission rate percentage.")
    public ResponseEntity<ApiResponse<AgentCommissionSummaryDTO>> calculateCommissionWithCustomRate(
            @Valid @RequestBody CommissionCalculationRequestDTO request) {
        AgentCommissionSummaryDTO summary = commissionCalculatorService.calculateCommissionWithRequest(request);
        return ResponseEntity.ok(ApiResponse.ok("Commission calculated with specified rate successfully", summary));
    }
}
