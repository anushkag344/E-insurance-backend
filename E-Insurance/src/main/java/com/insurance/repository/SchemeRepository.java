package com.insurance.repository;

import com.insurance.model.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Integer> {
    List<Scheme> findByPlanPlanId(Integer planId);
    boolean existsBySchemeName(String schemeName);
}
