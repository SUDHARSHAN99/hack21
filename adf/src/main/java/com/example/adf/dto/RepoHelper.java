package com.example.adf.dto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adf.repo.LeadBidStatusRepo;

@Service
public class RepoHelper {

	@Autowired
	private InvestorDecisionRuleRepository nvestorDecisionRuleRepository;

	public List<InvestorDecisionRule> saveAffilcateDecisionRulesList(List<InvestorDecisionRule> decisionRules) {
		return nvestorDecisionRuleRepository.saveAll(decisionRules);
	}
	
	@Autowired
	private LeadBidStatusRepo leadBidStatusRepo;

	public List<LeadBidStatus> saveAffilcateLeadBidStatusList(List<LeadBidStatus> leadBidStatus) {
		return leadBidStatusRepo.saveAll(leadBidStatus);
	}
	
	public List<LeadBidStatus> findAllLeadBidStatusList() {
		return leadBidStatusRepo.findAll();
	}
}
