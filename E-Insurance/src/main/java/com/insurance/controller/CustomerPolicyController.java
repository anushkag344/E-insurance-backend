package com.insurance.controller;

import com.insurance.dto.ApiResponse;
import com.insurance.dto.PaymentResponseDTO;
import com.insurance.dto.PolicyPurchaseRequestDTO;
import com.insurance.dto.PolicyResponseDTO;
import com.insurance.dto.SchemeDTO;
import com.insurance.service.PolicyService;
import com.insurance.service.SchemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/customer")
@Tag(name = "Customer Operations", description = "Endpoints for Customer policy purchase and policy/payment viewing (Use Cases 2 and 4)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerPolicyController {

    private final PolicyService policyService;
    private final SchemeService schemeService;

    public CustomerPolicyController(PolicyService policyService, SchemeService schemeService) {
        this.policyService = policyService;
        this.schemeService = schemeService;
    }

    @GetMapping("/policies")
    @Operation(summary = "UC2: View My Policies", description = "Retrieves all policies and payment records for the logged-in customer.")
    public ResponseEntity<ApiResponse<List<PolicyResponseDTO>>> getMyPolicies(Authentication authentication) {
        String username = authentication.getName();
        List<PolicyResponseDTO> policies = policyService.getMyPolicies(username);
        return ResponseEntity.ok(ApiResponse.ok("Policies retrieved successfully", policies));
    }

    @GetMapping("/payments")
    @Operation(summary = "UC2: View My Payments", description = "Retrieves all payment transactions for the logged-in customer.")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getMyPayments(Authentication authentication) {
        String username = authentication.getName();
        List<PaymentResponseDTO> payments = policyService.getMyPayments(username);
        return ResponseEntity.ok(ApiResponse.ok("Payments retrieved successfully", payments));
    }

    @GetMapping("/schemes")
    @Operation(summary = "UC4 Step 2: Browse Available Schemes", description = "Retrieves all available insurance schemes for purchase.")
    public ResponseEntity<ApiResponse<List<SchemeDTO>>> getAvailableSchemes() {
        List<SchemeDTO> schemes = schemeService.getAllSchemes();
        return ResponseEntity.ok(ApiResponse.ok("Available schemes retrieved successfully", schemes));
    }

    @PostMapping("/policies/purchase")
    @Operation(summary = "UC4: Purchase Policy", description = "Customer purchases an insurance policy and records the initial payment.")
    public ResponseEntity<ApiResponse<PolicyResponseDTO>> purchasePolicy(
            Authentication authentication,
            @Valid @RequestBody PolicyPurchaseRequestDTO request) {
        String username = authentication.getName();
        PolicyResponseDTO policy = policyService.purchasePolicy(username, request);
        return new ResponseEntity<>(
                ApiResponse.ok("Policy purchased successfully", policy),
                HttpStatus.CREATED
        );
    }
}

