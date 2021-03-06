package com.example.adf.dto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.adf.repo.LeadBidStatusRepo;
import com.example.adf.repo.LeadModelScoresRepo;

@Service
public class RepoHelper {

	@Autowired
	private InvestorDecisionRuleRepository nvestorDecisionRuleRepository;
	
	@Autowired
	private LeadBidStatusRepo leadBidStatusRepo;
	@Autowired
	private LeadModelScoresRepo leadModelScoresrepo;


	public List<InvestorDecisionRule> saveDecisionRulesList(List<InvestorDecisionRule> decisionRules) {
		return nvestorDecisionRuleRepository.saveAll(decisionRules);
	}
	
	public List<LeadBidStatus> saveLeadBidStatusList(List<LeadBidStatus> leadBidStatus) {
		return leadBidStatusRepo.saveAll(leadBidStatus);
	}
	
	public List<LeadBidStatus> findAllLeadBidStatusList() {
		return leadBidStatusRepo.findAll();
	}
	
	public List<InvestorDecisionRule> findByListingId(Long leadId) {
		return nvestorDecisionRuleRepository.findByLeadId(leadId);
	}
	
	public LeadModelScores saveleadModelScoresList(LeadModelScores leadModelScores) {
		return leadModelScoresrepo.save(leadModelScores);
	}

	public LeadModelScores findByListingIdOnModelScores(long parseLong) {
		return leadModelScoresrepo.findByLeadId(parseLong);
	}
}
