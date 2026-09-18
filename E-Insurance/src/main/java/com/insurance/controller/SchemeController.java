package com.insurance.controller;

import com.insurance.dto.ApiResponse;
import com.insurance.dto.InsurancePlanDTO;
import com.insurance.dto.SchemeDTO;
import com.insurance.service.SchemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/schemes")
@Tag(name = "Insurance Schemes & Plans", description = "Public / general endpoints to browse available insurance plans and schemes")
public class SchemeController {

    private final SchemeService schemeService;

    public SchemeController(SchemeService schemeService) {
        this.schemeService = schemeService;
    }

    @GetMapping
    @Operation(summary = "Get All Schemes", description = "Retrieve all available insurance schemes.")
    public ResponseEntity<ApiResponse<List<SchemeDTO>>> getAllSchemes() {
        return ResponseEntity.ok(ApiResponse.ok("Schemes retrieved successfully", schemeService.getAllSchemes()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Scheme by ID", description = "Retrieve details of a specific scheme.")
    public ResponseEntity<ApiResponse<SchemeDTO>> getSchemeById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Scheme retrieved successfully", schemeService.getSchemeById(id)));
    }

    @GetMapping("/plans")
    @Operation(summary = "Get All Plans", description = "Retrieve all insurance plans with nested schemes.")
    public ResponseEntity<ApiResponse<List<InsurancePlanDTO>>> getAllPlans() {
        return ResponseEntity.ok(ApiResponse.ok("Plans retrieved successfully", schemeService.getAllPlans()));
    }

    @GetMapping("/plan/{planId}")
    @Operation(summary = "Get Schemes by Plan ID", description = "Retrieve schemes under a specific plan.")
    public ResponseEntity<ApiResponse<List<SchemeDTO>>> getSchemesByPlanId(@PathVariable Integer planId) {
        return ResponseEntity.ok(ApiResponse.ok("Schemes for plan retrieved successfully", schemeService.getSchemesByPlanId(planId)));
    }
}

