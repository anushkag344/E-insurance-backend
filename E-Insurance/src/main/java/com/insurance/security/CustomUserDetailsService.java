package com.insurance.security;

import com.insurance.model.Admin;
import com.insurance.model.Customer;
import com.insurance.model.Employee;
import com.insurance.model.InsuranceAgent;
import com.insurance.repository.AdminRepository;
import com.insurance.repository.CustomerRepository;
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
    private final InsuranceAgentRepository insuranceAgentRepository;
    private final CustomerRepository customerRepository;

    public CustomUserDetailsService(
            AdminRepository adminRepository,
            EmployeeRepository employeeRepository,
            InsuranceAgentRepository insuranceAgentRepository,
            CustomerRepository customerRepository) {

        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
        this.insuranceAgentRepository = insuranceAgentRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        var adminOptional =
                adminRepository.findByUsername(username);

        if (adminOptional.isPresent()) {

            Admin admin = adminOptional.get();

            return User.builder()
                    .username(admin.getUsername())
                    .password(admin.getPassword())
                    .authorities(
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(
                                            "ROLE_ADMIN"
                                    )
                            )
                    )
                    .build();
        }

        var employeeOptional =
                employeeRepository.findByUsername(username);

        if (employeeOptional.isPresent()) {

            Employee employee = employeeOptional.get();

            return User.builder()
                    .username(employee.getUsername())
                    .password(employee.getPassword())
                    .authorities(
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + employee.getRole()
                                    )
                            )
                    )
                    .build();
        }


        var agentOptional =
                insuranceAgentRepository.findByUsername(username);

        if (agentOptional.isPresent()) {

            InsuranceAgent agent =
                    agentOptional.get();

            return User.builder()
                    .username(agent.getUsername())
                    .password(agent.getPassword())
                    .authorities(
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(
                                            "ROLE_AGENT"
                                    )
                            )
                    )
                    .build();
        }
        var customerOptional =
                customerRepository.findByUsername(username);

        if (customerOptional.isPresent()) {

            Customer customer =
                    customerOptional.get();

            return User.builder()
                    .username(customer.getUsername())
                    .password(customer.getPassword())
                    .authorities(
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(
                                            "ROLE_CUSTOMER"
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