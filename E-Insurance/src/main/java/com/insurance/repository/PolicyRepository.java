package com.insurance.repository;

import com.insurance.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {
    List<Policy> findByCustomerCustomerId(Integer customerId);
    List<Policy> findByCustomerUsername(String username);
    List<Policy> findBySchemeSchemeId(Integer schemeId);
    List<Policy> findByCustomerAgentAgentId(Integer agentId);
}
