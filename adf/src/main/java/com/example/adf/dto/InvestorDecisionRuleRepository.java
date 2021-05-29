package com.example.adf.dto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestorDecisionRuleRepository extends JpaRepository<InvestorDecisionRule, Long> {

	List<InvestorDecisionRule> findByLeadId(Long leadId);
}
