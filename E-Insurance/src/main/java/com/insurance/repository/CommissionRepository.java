package com.insurance.repository;

import com.insurance.model.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Integer> {
    List<Commission> findByAgentAgentId(Integer agentId);
    List<Commission> findByPolicyPolicyId(Integer policyId);
}
