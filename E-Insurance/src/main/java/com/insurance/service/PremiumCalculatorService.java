package com.insurance.service;

import com.insurance.dto.PremiumCalculateRequestDTO;
import com.insurance.dto.PremiumCalculateResponseDTO;
import com.insurance.exception.ResourceNotFoundException;
import com.insurance.model.Scheme;
import com.insurance.repository.SchemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PremiumCalculatorService {

    private final SchemeRepository schemeRepository;

    public PremiumCalculatorService(SchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
    }

    @Transactional(readOnly = true)
    public PremiumCalculateResponseDTO calculatePremium(PremiumCalculateRequestDTO request) {
        String schemeName = null;
        String planName = null;

        if (request.getSchemeId() != null) {
            Scheme scheme = schemeRepository.findById(request.getSchemeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with id: " + request.getSchemeId()));
            schemeName = scheme.getSchemeName();
            if (scheme.getPlan() != null) {
                planName = scheme.getPlan().getPlanName();
            }
        }

        BigDecimal sumAssured = request.getSumAssured();
        int age = request.getAge();
        int termYears = request.getTermYears();

        BigDecimal roi = request.getRateOfInterest() != null
                ? request.getRateOfInterest().setScale(2, RoundingMode.HALF_UP)
                : new BigDecimal("6.00");

        String frequency = request.getPaymentFrequency() != null
                ? request.getPaymentFrequency().trim().toUpperCase()
                : "ANNUAL";

        int installmentsPerYear;
        BigDecimal frequencyFactor;

        switch (frequency) {
            case "MONTHLY":
                installmentsPerYear = 12;
                frequencyFactor = new BigDecimal("0.0875"); // ~1.05 / 12
                break;
            case "QUARTERLY":
                installmentsPerYear = 4;
                frequencyFactor = new BigDecimal("0.2575"); // ~1.03 / 4
                break;
            case "HALF_YEARLY":
            case "SEMI_ANNUAL":
                frequency = "HALF_YEARLY";
                installmentsPerYear = 2;
                frequencyFactor = new BigDecimal("0.5100"); // ~1.02 / 2
                break;
            case "ANNUAL":
            default:
                frequency = "ANNUAL";
                installmentsPerYear = 1;
                frequencyFactor = BigDecimal.ONE;
                break;
        }

        // Base Annual Premium: Sum Assured divided by policy term
        BigDecimal baseAnnual = sumAssured.divide(BigDecimal.valueOf(termYears), 4, RoundingMode.HALF_UP);

        // Age factor: Base is 1.0 for age <= 30. For age > 30, add 1.5% risk loading per year
        double ageFactor = 1.0;
        if (age > 30) {
            ageFactor += (age - 30) * 0.015;
        }

        // ROI Discount factor: Higher interest yield reduces net risk premium requirement
        double roiDiscount = 1.0 - (roi.doubleValue() / 100.0 * 0.25);
        if (roiDiscount < 0.70) {
            roiDiscount = 0.70;
        }

        BigDecimal adjustedFactor = BigDecimal.valueOf(ageFactor * roiDiscount);
        BigDecimal annualPremium = baseAnnual.multiply(adjustedFactor).setScale(2, RoundingMode.HALF_UP);

        // Installment Premium per payment cycle
        BigDecimal installmentPremium;
        if ("ANNUAL".equals(frequency)) {
            installmentPremium = annualPremium;
        } else {
            installmentPremium = annualPremium.multiply(frequencyFactor).setScale(2, RoundingMode.HALF_UP);
        }

        int totalInstallments = termYears * installmentsPerYear;
        BigDecimal totalPremiumPayable = installmentPremium.multiply(BigDecimal.valueOf(totalInstallments))
                .setScale(2, RoundingMode.HALF_UP);

        // Estimated maturity amount = Sum Assured + Simple Interest bonus
        BigDecimal totalInterest = sumAssured
                .multiply(roi)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(termYears));
        BigDecimal estimatedMaturityAmount = sumAssured.add(totalInterest).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalBenefit = estimatedMaturityAmount.subtract(totalPremiumPayable).setScale(2, RoundingMode.HALF_UP);

        String summary = String.format(
                "Premium calculated for %s term of %d years with %s payment frequency at %s%% ROI. " +
                "Estimated maturity payout: %s (Total estimated net benefit: %s).",
                schemeName != null ? "'" + schemeName + "'" : "general policy",
                termYears,
                frequency,
                roi.toPlainString(),
                estimatedMaturityAmount.toPlainString(),
                totalBenefit.toPlainString()
        );

        return PremiumCalculateResponseDTO.builder()
                .schemeId(request.getSchemeId())
                .schemeName(schemeName)
                .planName(planName)
                .sumAssured(sumAssured.setScale(2, RoundingMode.HALF_UP))
                .age(age)
                .termYears(termYears)
                .paymentFrequency(frequency)
                .rateOfInterest(roi)
                .annualPremium(annualPremium)
                .installmentPremium(installmentPremium)
                .totalInstallments(totalInstallments)
                .totalPremiumPayable(totalPremiumPayable)
                .estimatedMaturityAmount(estimatedMaturityAmount)
                .totalBenefit(totalBenefit)
                .calculationSummary(summary)
                .build();
    }
}
