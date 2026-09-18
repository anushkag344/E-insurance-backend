package com.insurance.service;

import com.insurance.dto.*;
import com.insurance.exception.ResourceNotFoundException;
import com.insurance.model.*;
import com.insurance.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final InsuranceAgentRepository insuranceAgentRepository;
    private final BankRepository bankRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(
            CustomerRepository customerRepository,
            EmployeeRepository employeeRepository,
            InsuranceAgentRepository insuranceAgentRepository,
            BankRepository bankRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.insuranceAgentRepository = insuranceAgentRepository;
        this.bankRepository = bankRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // CUSTOMER CRUD (UC3)
    // ==========================================

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToCustomerResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToCustomerResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Integer id, CustomerUpdateDTO request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) {
            customer.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getAgentId() != null) {
            InsuranceAgent agent = insuranceAgentRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + request.getAgentId()));
            customer.setAgent(agent);
        }

        Customer updated = customerRepository.save(customer);
        return mapToCustomerResponseDTO(updated);
    }

    @Transactional
    public void deleteCustomer(Integer id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    // ==========================================
    // EMPLOYEE CRUD (UC3)
    // ==========================================

    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToEmployeeResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToEmployeeResponseDTO(employee);
    }

    @Transactional
    public EmployeeResponseDTO createEmployee(RegisterRequestDTO request) {
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Employee username already exists");
        }
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Employee email already exists");
        }

        Employee employee = Employee.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole() != null ? request.getRole().name() : "EMPLOYEE")
                .build();

        Employee saved = employeeRepository.save(employee);
        return mapToEmployeeResponseDTO(saved);
    }

    @Transactional
    public EmployeeResponseDTO updateEmployee(Integer id, EmployeeUpdateDTO request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setRole(request.getRole());

        Employee updated = employeeRepository.save(employee);
        return mapToEmployeeResponseDTO(updated);
    }

    @Transactional
    public void deleteEmployee(Integer id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    // ==========================================
    // AGENT CRUD (UC3)
    // ==========================================

    @Transactional(readOnly = true)
    public List<InsuranceAgentResponseDTO> getAllAgents() {
        return insuranceAgentRepository.findAll().stream()
                .map(this::mapToAgentResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InsuranceAgentResponseDTO getAgentById(Integer id) {
        InsuranceAgent agent = insuranceAgentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + id));
        return mapToAgentResponseDTO(agent);
    }

    @Transactional
    public InsuranceAgentResponseDTO createAgent(RegisterRequestDTO request) {
        if (insuranceAgentRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Agent username already exists");
        }
        if (insuranceAgentRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Agent email already exists");
        }

        InsuranceAgent agent = InsuranceAgent.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .build();

        InsuranceAgent saved = insuranceAgentRepository.save(agent);
        return mapToAgentResponseDTO(saved);
    }

    @Transactional
    public InsuranceAgentResponseDTO updateAgent(Integer id, InsuranceAgentUpdateDTO request) {
        InsuranceAgent agent = insuranceAgentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + id));

        agent.setFullName(request.getFullName());
        agent.setEmail(request.getEmail());

        InsuranceAgent updated = insuranceAgentRepository.save(agent);
        return mapToAgentResponseDTO(updated);
    }

    @Transactional
    public void deleteAgent(Integer id) {
        if (!insuranceAgentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agent not found with id: " + id);
        }
        insuranceAgentRepository.deleteById(id);
    }

    // ==========================================
    // BANK CRUD (UC3)
    // ==========================================

    @Transactional(readOnly = true)
    public List<BankDTO> getAllBanks() {
        return bankRepository.findAll().stream()
                .map(this::mapToBankDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BankDTO getBankById(Integer id) {
        Bank bank = bankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found with id: " + id));
        return mapToBankDTO(bank);
    }

    @Transactional
    public BankDTO createBank(BankDTO request) {
        if (bankRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new IllegalArgumentException("Bank with account number '" + request.getAccountNumber() + "' already exists");
        }

        Bank bank = Bank.builder()
                .bankName(request.getBankName())
                .branchName(request.getBranchName())
                .ifscCode(request.getIfscCode())
                .accountNumber(request.getAccountNumber())
                .build();

        Bank saved = bankRepository.save(bank);
        return mapToBankDTO(saved);
    }

    @Transactional
    public BankDTO updateBank(Integer id, BankDTO request) {
        Bank bank = bankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found with id: " + id));

        bank.setBankName(request.getBankName());
        bank.setBranchName(request.getBranchName());
        bank.setIfscCode(request.getIfscCode());
        bank.setAccountNumber(request.getAccountNumber());

        Bank updated = bankRepository.save(bank);
        return mapToBankDTO(updated);
    }

    @Transactional
    public void deleteBank(Integer id) {
        if (!bankRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bank not found with id: " + id);
        }
        bankRepository.deleteById(id);
    }

    // ==========================================
    // MAPPER METHODS
    // ==========================================

    private CustomerResponseDTO mapToCustomerResponseDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .customerId(customer.getCustomerId())
                .username(customer.getUsername())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .dateOfBirth(customer.getDateOfBirth())
                .agentId(customer.getAgent() != null ? customer.getAgent().getAgentId() : null)
                .agentName(customer.getAgent() != null ? customer.getAgent().getFullName() : null)
                .createdAt(customer.getCreatedAt())
                .build();
    }

    private EmployeeResponseDTO mapToEmployeeResponseDTO(Employee employee) {
        return EmployeeResponseDTO.builder()
                .employeeId(employee.getEmployeeId())
                .username(employee.getUsername())
                .email(employee.getEmail())
                .fullName(employee.getFullName())
                .role(employee.getRole())
                .createdAt(employee.getCreatedAt())
                .build();
    }

    private InsuranceAgentResponseDTO mapToAgentResponseDTO(InsuranceAgent agent) {
        return InsuranceAgentResponseDTO.builder()
                .agentId(agent.getAgentId())
                .username(agent.getUsername())
                .email(agent.getEmail())
                .fullName(agent.getFullName())
                .createdAt(agent.getCreatedAt())
                .build();
    }

    private BankDTO mapToBankDTO(Bank bank) {
        return BankDTO.builder()
                .bankId(bank.getBankId())
                .bankName(bank.getBankName())
                .branchName(bank.getBranchName())
                .ifscCode(bank.getIfscCode())
                .accountNumber(bank.getAccountNumber())
                .createdAt(bank.getCreatedAt())
                .build();
    }
}
