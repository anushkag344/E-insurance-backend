package com.insurance.service;

import com.insurance.dto.CustomerRegisterRequestDTO;
import com.insurance.dto.LoginRequestDTO;
import com.insurance.dto.LoginResponseDTO;
import com.insurance.dto.RegisterRequestDTO;
import com.insurance.model.Admin;
import com.insurance.model.Customer;
import com.insurance.model.Employee;
import com.insurance.model.InsuranceAgent;
import com.insurance.repository.AdminRepository;
import com.insurance.repository.CustomerRepository;
import com.insurance.repository.EmployeeRepository;
import com.insurance.repository.InsuranceAgentRepository;
import com.insurance.security.CustomUserDetailsService;
import com.insurance.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final InsuranceAgentRepository insuranceAgentRepository;
    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            AdminRepository adminRepository,
            EmployeeRepository employeeRepository,
            InsuranceAgentRepository insuranceAgentRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService) {

        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
        this.insuranceAgentRepository = insuranceAgentRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());

        String role = user.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        String token = jwtService.generateToken(user, role);

        return LoginResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .role(role)
                .message("Login successful")
                .build();
    }

    public String registerCustomer(CustomerRegisterRequestDTO request) {
        if (customerRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Customer username already exists");
        }

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Customer email already exists");
        }

        InsuranceAgent agent = null;
        if (request.getAgentId() != null) {
            agent = insuranceAgentRepository.findById(request.getAgentId()).orElse(null);
        }

        Customer customer = Customer.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .agent(agent)
                .build();

        customerRepository.save(customer);

        return "Customer registered successfully";
    }

    public String registerEmployee(RegisterRequestDTO request) {
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

        employeeRepository.save(employee);

        return "Employee registered successfully";
    }

    public String registerAgent(RegisterRequestDTO request) {
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

        insuranceAgentRepository.save(agent);

        return "Insurance Agent registered successfully";
    }

    public String registerAdmin(RegisterRequestDTO request) {
        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Admin username already exists");
        }

        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Admin email already exists");
        }

        Admin admin = Admin.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .build();

        adminRepository.save(admin);

        return "Admin registered successfully";
    }
}