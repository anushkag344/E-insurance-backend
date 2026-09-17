package com.insurance.security;

import com.insurance.model.Admin;
import com.insurance.model.Employee;
import com.insurance.model.InsuranceAgent;
import com.insurance.repository.AdminRepository;
import com.insurance.repository.EmployeeRepository;
import com.insurance.repository.InsuranceAgentRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final InsuranceAgentRepository agentRepository;

    public CustomUserDetailsService(
            AdminRepository adminRepository,
            EmployeeRepository employeeRepository,
            InsuranceAgentRepository agentRepository) {

        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
        this.agentRepository = agentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Search Admin
        var admin = adminRepository.findByUsername(username);

        if (admin.isPresent()) {

            Admin user = admin.get();

            return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(
                                            "ROLE_ADMIN"
                                    )
                            )
                    )
                    .build();
        }

        // Search Employee
        var employee =
                employeeRepository.findByUsername(username);

        if (employee.isPresent()) {

            Employee user = employee.get();

            return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + user.getRole()
                                    )
                            )
                    )
                    .build();
        }

        // Search Insurance Agent
        var agent =
                agentRepository.findByUsername(username);

        if (agent.isPresent()) {

            InsuranceAgent user = agent.get();

            return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(
                                            "ROLE_AGENT"
                                    )
                            )
                    )
                    .build();
        }

        throw new UsernameNotFoundException(
                "User not found: " + username
        );
    }
}