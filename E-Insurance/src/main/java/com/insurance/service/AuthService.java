package com.insurance.service;

import com.insurance.dto.LoginRequestDTO;
import com.insurance.dto.LoginResponseDTO;
import com.insurance.dto.RegisterRequestDTO;
import com.insurance.enums.Role;
import com.insurance.model.Admin;
import com.insurance.model.Employee;
import com.insurance.model.InsuranceAgent;
import com.insurance.repository.AdminRepository;
import com.insurance.repository.EmployeeRepository;
import com.insurance.repository.InsuranceAgentRepository;
import com.insurance.security.CustomUserDetailsService;
import com.insurance.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final InsuranceAgentRepository agentRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            AdminRepository adminRepository,
            EmployeeRepository employeeRepository,
            InsuranceAgentRepository agentRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService) {

        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
        this.agentRepository = agentRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(
            LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails user =
                userDetailsService.loadUserByUsername(
                        request.getUsername()
                );

        String role =
                user.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_", "");

        String token =
                jwtService.generateToken(
                        user,
                        role
                );

        return LoginResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .role(role)
                .message("Login successful")
                .build();
    }

    public String register(
            RegisterRequestDTO request) {

        if (request.getRole() == Role.CUSTOMER) {

            throw new IllegalArgumentException(
                    "Customer registration cannot be implemented with current schema because Customer table has no username/password fields."
            );
        }

        switch (request.getRole()) {

            case ADMIN:

                if (adminRepository.existsByUsername(
                        request.getUsername())) {

                    throw new IllegalArgumentException(
                            "Admin username already exists"
                    );
                }

                Admin admin = Admin.builder()
                        .username(request.getUsername())
                        .password(
                                passwordEncoder.encode(
                                        request.getPassword()
                                )
                        )
                        .email(request.getEmail())
                        .fullName(request.getFullName())
                        .build();

                adminRepository.save(admin);

                return "Admin registered successfully";


            case EMPLOYEE:

                if (employeeRepository.existsByUsername(
                        request.getUsername())) {

                    throw new IllegalArgumentException(
                            "Employee username already exists"
                    );
                }

                Employee employee = Employee.builder()
                        .username(request.getUsername())
                        .password(
                                passwordEncoder.encode(
                                        request.getPassword()
                                )
                        )
                        .email(request.getEmail())
                        .fullName(request.getFullName())
                        .role("EMPLOYEE")
                        .build();

                employeeRepository.save(employee);

                return "Employee registered successfully";


            case AGENT:

                if (agentRepository.existsByUsername(
                        request.getUsername())) {

                    throw new IllegalArgumentException(
                            "Agent username already exists"
                    );
                }

                InsuranceAgent agent =
                        InsuranceAgent.builder()
                                .username(
                                        request.getUsername()
                                )
                                .password(
                                        passwordEncoder.encode(
                                                request.getPassword()
                                        )
                                )
                                .email(
                                        request.getEmail()
                                )
                                .fullName(
                                        request.getFullName()
                                )
                                .build();

                agentRepository.save(agent);

                return "Insurance Agent registered successfully";
        }

        throw new IllegalArgumentException(
                "Invalid role"
        );
    }
}