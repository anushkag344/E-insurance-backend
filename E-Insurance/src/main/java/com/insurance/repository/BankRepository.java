package com.insurance.repository;

import com.insurance.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Integer> {
    Optional<Bank> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
