package com.insurance;

import com.insurance.dto.PremiumCalculateRequestDTO;
import com.insurance.dto.PremiumCalculateResponseDTO;
import com.insurance.exception.ResourceNotFoundException;
import com.insurance.model.InsurancePlan;
import com.insurance.model.Scheme;
import com.insurance.repository.SchemeRepository;
import com.insurance.service.PremiumCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumCalculatorTest {

    @Mock
    private SchemeRepository schemeRepository;

    @InjectMocks
    private PremiumCalculatorService premiumCalculatorService;

    private Scheme sampleScheme;

    @BeforeEach
    void setUp() {
        InsurancePlan plan = InsurancePlan.builder()
                .planId(1)
                .planName("Life Care Protection")
                .build();

        sampleScheme = Scheme.builder()
                .schemeId(101)
                .schemeName("Term Secure Gold")
                .plan(plan)
                .build();
    }

    @Test
    void testCalculatePremium_WithoutScheme_YoungAge() {
        PremiumCalculateRequestDTO request = PremiumCalculateRequestDTO.builder()
                .sumAssured(new BigDecimal("500000"))
                .age(25) // Age <= 30: factor is 1.0
                .termYears(10)
                .paymentFrequency("ANNUAL")
                .rateOfInterest(new BigDecimal("6.00"))
                .build();

        PremiumCalculateResponseDTO response = premiumCalculatorService.calculatePremium(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("500000.00"), response.getSumAssured());
        assertEquals(25, response.getAge());
        assertEquals(10, response.getTermYears());
        assertEquals("ANNUAL", response.getPaymentFrequency());
        assertEquals(10, response.getTotalInstallments());
        assertNotNull(response.getAnnualPremium());
        assertNotNull(response.getInstallmentPremium());
        assertNotNull(response.getEstimatedMaturityAmount());
        assertTrue(response.getEstimatedMaturityAmount().compareTo(response.getSumAssured()) > 0);
        assertNotNull(response.getTotalBenefit());
    }

    @Test
    void testCalculatePremium_WithScheme_HigherAge() {
        when(schemeRepository.findById(101)).thenReturn(Optional.of(sampleScheme));

        PremiumCalculateRequestDTO request = PremiumCalculateRequestDTO.builder()
                .schemeId(101)
                .sumAssured(new BigDecimal("1000000"))
                .age(45) // Age > 30: risk loading factor added
                .termYears(20)
                .paymentFrequency("MONTHLY")
                .rateOfInterest(new BigDecimal("7.50"))
                .build();

        PremiumCalculateResponseDTO response = premiumCalculatorService.calculatePremium(request);

        assertNotNull(response);
        assertEquals(101, response.getSchemeId());
        assertEquals("Term Secure Gold", response.getSchemeName());
        assertEquals("Life Care Protection", response.getPlanName());
        assertEquals(new BigDecimal("1000000.00"), response.getSumAssured());
        assertEquals("MONTHLY", response.getPaymentFrequency());
        assertEquals(240, response.getTotalInstallments()); // 20 years * 12 months
        assertTrue(response.getAnnualPremium().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(response.getInstallmentPremium().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(response.getInstallmentPremium().compareTo(response.getAnnualPremium()) < 0);
    }

    @Test
    void testCalculatePremium_QuarterlyFrequency() {
        PremiumCalculateRequestDTO request = PremiumCalculateRequestDTO.builder()
                .sumAssured(new BigDecimal("200000"))
                .age(30)
                .termYears(5)
                .paymentFrequency("QUARTERLY")
                .build(); // Default ROI 6%

        PremiumCalculateResponseDTO response = premiumCalculatorService.calculatePremium(request);

        assertNotNull(response);
        assertEquals("QUARTERLY", response.getPaymentFrequency());
        assertEquals(20, response.getTotalInstallments()); // 5 * 4
        assertEquals(new BigDecimal("6.00"), response.getRateOfInterest());
    }

    @Test
    void testCalculatePremium_SchemeNotFound() {
        when(schemeRepository.findById(999)).thenReturn(Optional.empty());

        PremiumCalculateRequestDTO request = PremiumCalculateRequestDTO.builder()
                .schemeId(999)
                .sumAssured(new BigDecimal("300000"))
                .age(35)
                .termYears(10)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> premiumCalculatorService.calculatePremium(request));
    }
}
