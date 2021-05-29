package com.example.adf.Helper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import com.example.adf.dto.InvestorDecisionRule;

@Service
public class InvestorRuleService {

	public static final String REJECT = "REJECT";
	public static final DateTimeZone PACIFIC_TZ = DateTimeZone.forID("US/Pacific");

	public static InvestorDecisionRule buildRule(long leadId, String ruleId) {
		InvestorDecisionRule rule = new InvestorDecisionRule();
		rule.setCreatedDtm(DateTime.now(PACIFIC_TZ).toLocalDateTime().toDate());
		rule.setLeadId(leadId);
		rule.setRuleName(ruleId);
		rule.setRuleDecision(REJECT);
		return rule;
	}

}
