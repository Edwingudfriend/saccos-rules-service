package com.reimagineafrica.rules.controller;

import com.reimagineafrica.rules.dto.RuleDto.*;
import com.reimagineafrica.rules.service.RuleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RuleResponse>> getAllRules(HttpServletRequest request) {
        return ResponseEntity.ok(ruleService.getAllRules(tenantId(request)));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RuleResponse>> getByCategory(
            @PathVariable String category, HttpServletRequest request) {
        return ResponseEntity.ok(ruleService.getRulesByCategory(tenantId(request), category));
    }

    @GetMapping("/{ruleCode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RuleResponse> getRule(
            @PathVariable String ruleCode, HttpServletRequest request) {
        return ResponseEntity.ok(RuleResponse.builder()
                .ruleCode(ruleCode)
                .ruleValue(ruleService.getRuleValue(tenantId(request), ruleCode))
                .build());
    }

    @PutMapping("/{ruleCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RuleResponse> updateRule(
            @PathVariable String ruleCode,
            @RequestBody UpdateRuleRequest request,
            HttpServletRequest httpRequest) {
        String updatedBy = (String) httpRequest.getAttribute("userId");
        return ResponseEntity.ok(
                ruleService.updateRule(tenantId(httpRequest), ruleCode, request, updatedBy));
    }

    @PostMapping("/evaluate/loan-eligibility")
    @PreAuthorize("hasAnyRole('ADMIN','LOAN_OFFICER','CREDIT_COMMITTEE')")
    public ResponseEntity<EligibilityResult> evaluateLoan(
            @RequestBody LoanEligibilityRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(
                ruleService.evaluateLoanEligibility(tenantId(httpRequest), request));
    }

    private String tenantId(HttpServletRequest r) {
        String t = (String) r.getAttribute("tenantId");
        return t != null ? t : "default";
    }
}
