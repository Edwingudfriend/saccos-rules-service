package com.reimagineafrica.rules.repository;

import com.reimagineafrica.rules.entity.SaccoRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<SaccoRule, String> {
    Optional<SaccoRule> findByTenantIdAndRuleCodeAndActiveTrue(String tenantId, String ruleCode);
    List<SaccoRule> findByTenantIdAndActiveTrue(String tenantId);
    List<SaccoRule> findByTenantIdAndCategoryAndActiveTrue(String tenantId, String category);
    boolean existsByTenantIdAndRuleCode(String tenantId, String ruleCode);
}
