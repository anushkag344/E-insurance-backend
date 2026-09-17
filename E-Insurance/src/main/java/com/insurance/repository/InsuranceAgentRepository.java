package com.insurance.repository;

import com.insurance.model.InsuranceAgent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsuranceAgentRepository
        extends JpaRepository<InsuranceAgent, Integer> {

    Optional<InsuranceAgent> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}