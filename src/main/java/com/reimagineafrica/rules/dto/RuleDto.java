package com.reimagineafrica.rules.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RuleDto {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RuleResponse {
        private String id;
        private String ruleCode;
        private String ruleValue;
        private String dataType;
        private String category;
        private String description;
        private LocalDate effectiveFrom;
        private LocalDate effectiveTo;
        private boolean active;
        private String updatedBy;
        private LocalDateTime updatedAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRuleRequest {
        private String ruleValue;
        private String description;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LoanEligibilityRequest {
        private String memberId;
        private String memberStatus;
        private boolean kycVerified;
        private int totalShares;
        private int savingsMonths;
        private int guarantorCount;
        private BigDecimal requestedAmount;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EligibilityResult {
        private boolean eligible;
        private BigDecimal maxLoanAmount;
        private boolean sharesCheck;
        private boolean savingsCheck;
        private boolean guarantorCheck;
        private boolean kycCheck;
        private boolean activeCheck;
        private String message;
    }
}
