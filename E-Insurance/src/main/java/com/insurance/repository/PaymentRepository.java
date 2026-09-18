package com.insurance.repository;

import com.insurance.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByCustomerCustomerId(Integer customerId);
    List<Payment> findByCustomerUsername(String username);
    List<Payment> findByPolicyPolicyId(Integer policyId);
}
