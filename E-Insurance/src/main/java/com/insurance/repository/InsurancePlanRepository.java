package com.insurance.repository;

import com.insurance.model.InsurancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, Integer> {
    Optional<InsurancePlan> findByPlanName(String planName);
    boolean existsByPlanName(String planName);
}
