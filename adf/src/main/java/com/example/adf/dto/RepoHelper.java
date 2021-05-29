package com.example.adf.dto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepoHelper {

	@Autowired
	private InvestorDecisionRuleRepository nvestorDecisionRuleRepository;

	public List<InvestorDecisionRule> saveAffilcateDecisionRulesList(List<InvestorDecisionRule> decisionRules) {
		return nvestorDecisionRuleRepository.saveAll(decisionRules);
	}
}
