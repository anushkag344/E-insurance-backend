package com.insurance.controller;

import com.insurance.dto.*;
import com.insurance.service.AdminService;
import com.insurance.service.PolicyService;
import com.insurance.service.SchemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admin")
@Tag(name = "Admin Operations", description = "Admin endpoints for User Management, Bank Management, Policy Auditing, and Plans/Schemes (Use Cases 2 and 3)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final PolicyService policyService;
    private final SchemeService schemeService;

    public AdminController(AdminService adminService, PolicyService policyService, SchemeService schemeService) {
        this.adminService = adminService;
        this.policyService = policyService;
        this.schemeService = schemeService;
    }

    // ==========================================
    // UC2: VIEW CUSTOMER POLICIES & PAYMENTS
    // ==========================================

    @GetMapping("/customers/{customerId}/policies")
    @Operation(summary = "UC2: View Customer Policies (Admin)", description = "Admin views all policies and payment details for a specific customer.")
    public ResponseEntity<ApiResponse<List<PolicyResponseDTO>>> getCustomerPolicies(@PathVariable Integer customerId) {
        List<PolicyResponseDTO> policies = policyService.getPoliciesByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.ok("Customer policies retrieved successfully", policies));
    }

    @GetMapping("/customers/{customerId}/payments")
    @Operation(summary = "UC2: View Customer Payments (Admin)", description = "Admin views payment history for a specific customer.")
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getCustomerPayments(@PathVariable Integer customerId) {
        List<PaymentResponseDTO> payments = policyService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.ok("Customer payments retrieved successfully", payments));
    }

    // ==========================================
    // UC3: MANAGE CUSTOMERS (CRUD)
    // ==========================================

    @GetMapping("/customers")
    @Operation(summary = "UC3: List All Customers", description = "Admin lists all registered customers.")
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.ok("Customers retrieved successfully", adminService.getAllCustomers()));
    }

    @GetMapping("/customers/{id}")
    @Operation(summary = "UC3: Get Customer by ID", description = "Admin fetches customer details by ID.")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getCustomerById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Customer details retrieved", adminService.getCustomerById(id)));
    }

    @PutMapping("/customers/{id}")
    @Operation(summary = "UC3: Update Customer", description = "Admin updates customer profile information.")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> updateCustomer(
            @PathVariable Integer id,
            @Valid @RequestBody CustomerUpdateDTO request) {
        return ResponseEntity.ok(ApiResponse.ok("Customer updated successfully", adminService.updateCustomer(id, request)));
    }

    @DeleteMapping("/customers/{id}")
    @Operation(summary = "UC3: Delete Customer", description = "Admin deletes a customer.")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Integer id) {
        adminService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.ok("Customer deleted successfully"));
    }

    // ==========================================
    // UC3: MANAGE EMPLOYEES (CRUD)
    // ==========================================

    @GetMapping("/employees")
    @Operation(summary = "UC3: List All Employees", description = "Admin lists all employees.")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> getAllEmployees() {
        return ResponseEntity.ok(ApiResponse.ok("Employees retrieved successfully", adminService.getAllEmployees()));
    }

    @GetMapping("/employees/{id}")
    @Operation(summary = "UC3: Get Employee by ID", description = "Admin fetches an employee by ID.")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Employee details retrieved", adminService.getEmployeeById(id)));
    }

    @PostMapping("/employees")
    @Operation(summary = "UC3: Create Employee", description = "Admin creates a new employee account.")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> createEmployee(@Valid @RequestBody RegisterRequestDTO request) {
        EmployeeResponseDTO employee = adminService.createEmployee(request);
        return new ResponseEntity<>(ApiResponse.ok("Employee created successfully", employee), HttpStatus.CREATED);
    }

    @PutMapping("/employees/{id}")
    @Operation(summary = "UC3: Update Employee", description = "Admin updates employee details.")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable Integer id,
            @Valid @RequestBody EmployeeUpdateDTO request) {
        return ResponseEntity.ok(ApiResponse.ok("Employee updated successfully", adminService.updateEmployee(id, request)));
    }

    @DeleteMapping("/employees/{id}")
    @Operation(summary = "UC3: Delete Employee", description = "Admin deletes an employee.")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Integer id) {
        adminService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee deleted successfully"));
    }

    // ==========================================
    // UC3: MANAGE INSURANCE AGENTS (CRUD)
    // ==========================================

    @GetMapping("/agents")
    @Operation(summary = "UC3: List All Insurance Agents", description = "Admin lists all insurance agents.")
    public ResponseEntity<ApiResponse<List<InsuranceAgentResponseDTO>>> getAllAgents() {
        return ResponseEntity.ok(ApiResponse.ok("Agents retrieved successfully", adminService.getAllAgents()));
    }

    @GetMapping("/agents/{id}")
    @Operation(summary = "UC3: Get Agent by ID", description = "Admin fetches an agent by ID.")
    public ResponseEntity<ApiResponse<InsuranceAgentResponseDTO>> getAgentById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Agent details retrieved", adminService.getAgentById(id)));
    }

    @PostMapping("/agents")
    @Operation(summary = "UC3: Create Insurance Agent", description = "Admin creates a new insurance agent account.")
    public ResponseEntity<ApiResponse<InsuranceAgentResponseDTO>> createAgent(@Valid @RequestBody RegisterRequestDTO request) {
        InsuranceAgentResponseDTO agent = adminService.createAgent(request);
        return new ResponseEntity<>(ApiResponse.ok("Agent created successfully", agent), HttpStatus.CREATED);
    }

    @PutMapping("/agents/{id}")
    @Operation(summary = "UC3: Update Insurance Agent", description = "Admin updates agent details.")
    public ResponseEntity<ApiResponse<InsuranceAgentResponseDTO>> updateAgent(
            @PathVariable Integer id,
            @Valid @RequestBody InsuranceAgentUpdateDTO request) {
        return ResponseEntity.ok(ApiResponse.ok("Agent updated successfully", adminService.updateAgent(id, request)));
    }

    @DeleteMapping("/agents/{id}")
    @Operation(summary = "UC3: Delete Insurance Agent", description = "Admin deletes an agent.")
    public ResponseEntity<ApiResponse<Void>> deleteAgent(@PathVariable Integer id) {
        adminService.deleteAgent(id);
        return ResponseEntity.ok(ApiResponse.ok("Agent deleted successfully"));
    }

    // ==========================================
    // UC3: MANAGE BANKS (CRUD)
    // ==========================================

    @GetMapping("/banks")
    @Operation(summary = "UC3: List All Banks", description = "Admin lists all configured banks.")
    public ResponseEntity<ApiResponse<List<BankDTO>>> getAllBanks() {
        return ResponseEntity.ok(ApiResponse.ok("Banks retrieved successfully", adminService.getAllBanks()));
    }

    @GetMapping("/banks/{id}")
    @Operation(summary = "UC3: Get Bank by ID", description = "Admin fetches a bank by ID.")
    public ResponseEntity<ApiResponse<BankDTO>> getBankById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Bank details retrieved", adminService.getBankById(id)));
    }

    @PostMapping("/banks")
    @Operation(summary = "UC3: Create Bank", description = "Admin creates a new bank record.")
    public ResponseEntity<ApiResponse<BankDTO>> createBank(@Valid @RequestBody BankDTO request) {
        BankDTO created = adminService.createBank(request);
        return new ResponseEntity<>(ApiResponse.ok("Bank created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/banks/{id}")
    @Operation(summary = "UC3: Update Bank", description = "Admin updates bank information.")
    public ResponseEntity<ApiResponse<BankDTO>> updateBank(
            @PathVariable Integer id,
            @Valid @RequestBody BankDTO request) {
        return ResponseEntity.ok(ApiResponse.ok("Bank updated successfully", adminService.updateBank(id, request)));
    }

    @DeleteMapping("/banks/{id}")
    @Operation(summary = "UC3: Delete Bank", description = "Admin deletes a bank.")
    public ResponseEntity<ApiResponse<Void>> deleteBank(@PathVariable Integer id) {
        adminService.deleteBank(id);
        return ResponseEntity.ok(ApiResponse.ok("Bank deleted successfully"));
    }

    // ==========================================
    // PLAN & SCHEME MANAGEMENT (ADMIN)
    // ==========================================

    @PostMapping("/plans")
    @Operation(summary = "Create Insurance Plan", description = "Admin creates a new insurance plan category.")
    public ResponseEntity<ApiResponse<InsurancePlanDTO>> createPlan(@Valid @RequestBody InsurancePlanRequestDTO request) {
        InsurancePlanDTO plan = schemeService.createPlan(request);
        return new ResponseEntity<>(ApiResponse.ok("Plan created successfully", plan), HttpStatus.CREATED);
    }

    @GetMapping("/plans")
    @Operation(summary = "List Insurance Plans", description = "Admin lists all insurance plans and their schemes.")
    public ResponseEntity<ApiResponse<List<InsurancePlanDTO>>> getAllPlans() {
        return ResponseEntity.ok(ApiResponse.ok("Plans retrieved successfully", schemeService.getAllPlans()));
    }

    @PostMapping("/schemes")
    @Operation(summary = "Create Scheme", description = "Admin creates a new scheme under a plan.")
    public ResponseEntity<ApiResponse<SchemeDTO>> createScheme(@Valid @RequestBody SchemeRequestDTO request) {
        SchemeDTO scheme = schemeService.createScheme(request);
        return new ResponseEntity<>(ApiResponse.ok("Scheme created successfully", scheme), HttpStatus.CREATED);
    }

    @GetMapping("/schemes")
    @Operation(summary = "List Schemes", description = "Admin lists all schemes.")
    public ResponseEntity<ApiResponse<List<SchemeDTO>>> getAllSchemes() {
        return ResponseEntity.ok(ApiResponse.ok("Schemes retrieved successfully", schemeService.getAllSchemes()));
    }
}

