package com.insurance.service;

import com.insurance.dto.InsurancePlanDTO;
import com.insurance.dto.InsurancePlanRequestDTO;
import com.insurance.dto.SchemeDTO;
import com.insurance.dto.SchemeRequestDTO;
import com.insurance.exception.ResourceNotFoundException;
import com.insurance.model.InsurancePlan;
import com.insurance.model.Scheme;
import com.insurance.repository.InsurancePlanRepository;
import com.insurance.repository.SchemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchemeService {

    private final InsurancePlanRepository planRepository;
    private final SchemeRepository schemeRepository;

    public SchemeService(InsurancePlanRepository planRepository, SchemeRepository schemeRepository) {
        this.planRepository = planRepository;
        this.schemeRepository = schemeRepository;
    }

    @Transactional(readOnly = true)
    public List<InsurancePlanDTO> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::mapToPlanDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InsurancePlanDTO getPlanById(Integer planId) {
        InsurancePlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance Plan not found with id: " + planId));
        return mapToPlanDTO(plan);
    }

    @Transactional
    public InsurancePlanDTO createPlan(InsurancePlanRequestDTO request) {
        if (planRepository.existsByPlanName(request.getPlanName())) {
            throw new IllegalArgumentException("Plan with name '" + request.getPlanName() + "' already exists");
        }
        InsurancePlan plan = InsurancePlan.builder()
                .planName(request.getPlanName())
                .planDetails(request.getPlanDetails())
                .build();
        InsurancePlan saved = planRepository.save(plan);
        return mapToPlanDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<SchemeDTO> getAllSchemes() {
        return schemeRepository.findAll().stream()
                .map(this::mapToSchemeDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SchemeDTO> getSchemesByPlanId(Integer planId) {
        if (!planRepository.existsById(planId)) {
            throw new ResourceNotFoundException("Insurance Plan not found with id: " + planId);
        }
        return schemeRepository.findByPlanPlanId(planId).stream()
                .map(this::mapToSchemeDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SchemeDTO getSchemeById(Integer schemeId) {
        Scheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with id: " + schemeId));
        return mapToSchemeDTO(scheme);
    }

    @Transactional
    public SchemeDTO createScheme(SchemeRequestDTO request) {
        InsurancePlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance Plan not found with id: " + request.getPlanId()));

        if (schemeRepository.existsBySchemeName(request.getSchemeName())) {
            throw new IllegalArgumentException("Scheme with name '" + request.getSchemeName() + "' already exists");
        }

        Scheme scheme = Scheme.builder()
                .schemeName(request.getSchemeName())
                .schemeDetails(request.getSchemeDetails())
                .plan(plan)
                .build();

        Scheme saved = schemeRepository.save(scheme);
        return mapToSchemeDTO(saved);
    }

    public SchemeDTO mapToSchemeDTO(Scheme scheme) {
        return SchemeDTO.builder()
                .schemeId(scheme.getSchemeId())
                .schemeName(scheme.getSchemeName())
                .schemeDetails(scheme.getSchemeDetails())
                .planId(scheme.getPlan() != null ? scheme.getPlan().getPlanId() : null)
                .planName(scheme.getPlan() != null ? scheme.getPlan().getPlanName() : null)
                .createdAt(scheme.getCreatedAt())
                .build();
    }

    public InsurancePlanDTO mapToPlanDTO(InsurancePlan plan) {
        List<SchemeDTO> schemes = plan.getSchemes() != null
                ? plan.getSchemes().stream().map(this::mapToSchemeDTO).collect(Collectors.toList())
                : List.of();

        return InsurancePlanDTO.builder()
                .planId(plan.getPlanId())
                .planName(plan.getPlanName())
                .planDetails(plan.getPlanDetails())
                .createdAt(plan.getCreatedAt())
                .schemes(schemes)
                .build();
    }
}
