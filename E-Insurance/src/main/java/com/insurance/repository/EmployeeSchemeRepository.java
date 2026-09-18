package com.insurance.repository;

import com.insurance.model.EmployeeScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeSchemeRepository extends JpaRepository<EmployeeScheme, Integer> {
    List<EmployeeScheme> findByEmployeeEmployeeId(Integer employeeId);
    List<EmployeeScheme> findBySchemeSchemeId(Integer schemeId);
}
