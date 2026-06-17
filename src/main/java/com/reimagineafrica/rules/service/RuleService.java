package com.reimagineafrica.rules.service;

import com.reimagineafrica.rules.dto.RuleDto.*;
import com.reimagineafrica.rules.entity.SaccoRule;
import com.reimagineafrica.rules.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleService {

    private static final Logger log = LoggerFactory.getLogger(RuleService.class);
    private final RuleRepository ruleRepo;

    public String getRuleValue(String tenantId, String ruleCode) {
        return ruleRepo.findByTenantIdAndRuleCodeAndActiveTrue(tenantId, ruleCode)
                .map(SaccoRule::getRuleValue)
                .orElseThrow(() -> new RuntimeException("Rule not found: " + ruleCode));
    }

    public int getRuleInt(String tenantId, String ruleCode) {
        return Integer.parseInt(getRuleValue(tenantId, ruleCode));
    }

    public BigDecimal getRuleDecimal(String tenantId, String ruleCode) {
        return new BigDecimal(getRuleValue(tenantId, ruleCode));
    }

    public boolean getRuleBoolean(String tenantId, String ruleCode) {
        return Boolean.parseBoolean(getRuleValue(tenantId, ruleCode));
    }

    @Transactional(readOnly = true)
    public List<RuleResponse> getAllRules(String tenantId) {
        return ruleRepo.findByTenantIdAndActiveTrue(tenantId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RuleResponse> getRulesByCategory(String tenantId, String category) {
        return ruleRepo.findByTenantIdAndCategoryAndActiveTrue(tenantId, category.toUpperCase())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public RuleResponse updateRule(String tenantId, String ruleCode,
                                    UpdateRuleRequest request, String updatedBy) {
        SaccoRule rule = ruleRepo.findByTenantIdAndRuleCodeAndActiveTrue(tenantId, ruleCode)
                .orElseThrow(() -> new RuntimeException("Rule not found: " + ruleCode));
        rule.setRuleValue(request.getRuleValue());
        if (request.getDescription() != null) rule.setDescription(request.getDescription());
        rule.setUpdatedBy(updatedBy);
        SaccoRule saved = ruleRepo.save(rule);
        log.info("Rule updated — tenantId={}, ruleCode={}, newValue={}, by={}",
                tenantId, ruleCode, request.getRuleValue(), updatedBy);
        return toResponse(saved);
    }

    public EligibilityResult evaluateLoanEligibility(String tenantId,
                                                       LoanEligibilityRequest request) {
        int minShares       = getRuleInt(tenantId, "LOAN_MIN_SHARES");
        int minSavingMonths = getRuleInt(tenantId, "LOAN_MIN_SAVINGS_MONTHS");
        int minGuarantors   = getRuleInt(tenantId, "LOAN_MIN_GUARANTORS");
        BigDecimal maxMult  = getRuleDecimal(tenantId, "LOAN_MAX_MULTIPLIER");
        BigDecimal parValue = getRuleDecimal(tenantId, "SHARE_PAR_VALUE");

        boolean sharesOk    = request.getTotalShares() >= minShares;
        boolean savingsOk   = request.getSavingsMonths() >= minSavingMonths;
        boolean guarantorsOk= request.getGuarantorCount() >= minGuarantors;
        boolean kycOk       = request.isKycVerified();
        boolean activeOk    = "ACTIVE".equals(request.getMemberStatus());
        boolean eligible    = sharesOk && savingsOk && guarantorsOk && kycOk && activeOk;

        BigDecimal maxLoan  = parValue
                .multiply(BigDecimal.valueOf(request.getTotalShares()))
                .multiply(maxMult);

        return EligibilityResult.builder()
                .eligible(eligible)
                .maxLoanAmount(maxLoan)
                .sharesCheck(sharesOk)
                .savingsCheck(savingsOk)
                .guarantorCheck(guarantorsOk)
                .kycCheck(kycOk)
                .activeCheck(activeOk)
                .message(eligible ? "Member is eligible for a loan."
                        : buildMessage(sharesOk, savingsOk, guarantorsOk,
                                       kycOk, activeOk, minShares, minSavingMonths, minGuarantors))
                .build();
    }

    private String buildMessage(boolean shares, boolean savings, boolean guarantors,
                                 boolean kyc, boolean active,
                                 int minShares, int minMonths, int minGuarantors) {
        StringBuilder sb = new StringBuilder("Not eligible: ");
        if (!active)     sb.append("Member not active. ");
        if (!kyc)        sb.append("KYC not verified. ");
        if (!shares)     sb.append("Minimum ").append(minShares).append(" shares required. ");
        if (!savings)    sb.append("Minimum ").append(minMonths).append(" months savings required. ");
        if (!guarantors) sb.append("Minimum ").append(minGuarantors).append(" guarantor(s) required. ");
        return sb.toString().trim();
    }

    private RuleResponse toResponse(SaccoRule r) {
        return RuleResponse.builder()
                .id(r.getId())
                .ruleCode(r.getRuleCode())
                .ruleValue(r.getRuleValue())
                .dataType(r.getDataType())
                .category(r.getCategory())
                .description(r.getDescription())
                .effectiveFrom(r.getEffectiveFrom())
                .effectiveTo(r.getEffectiveTo())
                .active(r.isActive())
                .updatedBy(r.getUpdatedBy())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
