package com.example.adf.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.adf.dto.InvestorDecisionRule;
import com.example.adf.dto.Lead;
import com.example.adf.model.Result;

public class DataHelper {

	private static Map<Long, List<InvestorDecisionRule>> ruleMap = new HashMap<>();
	private static Map<Long, String> scoreMap = new HashMap<>();

	static public void addDecisionRules(Long leadId, InvestorDecisionRule rule) {
		List<InvestorDecisionRule> list = ruleMap.get(leadId);
		if (list == null) {
			list = new ArrayList<>();
			ruleMap.put(leadId, list);
		}
		list.add(rule);

		System.out.println("Rule added in entity holder for " + leadId + ":" + rule);
	}

	public static List<InvestorDecisionRule> getDecisionRules(Long leadId) {
		return ruleMap.get(leadId);
	}
	
	public static String getScoreMap(Long leadId) {
		return scoreMap.get(leadId);
	}
	
	public static String addScoreMap(Long leadId, String score) {
		return scoreMap.put(leadId, score);
	}

}
