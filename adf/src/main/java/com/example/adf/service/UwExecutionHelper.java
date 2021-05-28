package com.example.adf.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.example.adf.model.Result;
import com.github.jknack.handlebars.internal.lang3.StringUtils;

@Service
public class UwExecutionHelper {

	private ConcurrentHashMap<String, Boolean> modelScoreMap;
	private ConcurrentHashMap<String, Boolean> stateMap;

	@PostConstruct
	public void init() {
		buildScoreMap();
		buildStateMap();
	}
	// ["CO", "DC", "ID", "IN",
	// "IA", "KS", "LA", "ME",
	// "OK", "SC", "TX", "UT",
	// "WV", "WY", "PA"]

	private void buildStateMap() {
		stateMap = new ConcurrentHashMap<String, Boolean>();
		stateMap.put("CO", true);
		stateMap.put("DC", true);
		stateMap.put("ID", true);
		stateMap.put("IN", true);

		stateMap.put("IA", true);
		stateMap.put("KS", true);
		stateMap.put("LA", true);
		stateMap.put("ME", true);

		stateMap.put("OK", true);
		stateMap.put("SC", true);
		stateMap.put("TX", true);
		stateMap.put("UT", true);

		stateMap.put("WV", true);
		stateMap.put("WY", true);
		stateMap.put("PA", true);
	}

	private void buildScoreMap() {
		modelScoreMap = new ConcurrentHashMap<String, Boolean>();
		modelScoreMap.put("1_1", true);
		modelScoreMap.put("1_2", true);
		modelScoreMap.put("1_3", true);
		modelScoreMap.put("1_4", true);
		modelScoreMap.put("1_5", true);
		modelScoreMap.put("1_6", true);

		modelScoreMap.put("2_1", true);
		modelScoreMap.put("2_2", true);
		modelScoreMap.put("2_3", true);
		modelScoreMap.put("2_4", true);
		modelScoreMap.put("2_5", true);
		modelScoreMap.put("2_6", true);

		modelScoreMap.put("3_1", true);
		modelScoreMap.put("3_2", true);
		modelScoreMap.put("3_3", true);
		modelScoreMap.put("3_4", true);
		modelScoreMap.put("3_5", true);
		modelScoreMap.put("3_6", true);

		modelScoreMap.put("4_1", true);
		modelScoreMap.put("4_2", true);
		modelScoreMap.put("4_3", true);
		modelScoreMap.put("4_4", true);
		modelScoreMap.put("4_5", true);
		modelScoreMap.put("4_6", true);

		modelScoreMap.put("5_1", true);
		modelScoreMap.put("5_2", true);
		modelScoreMap.put("5_3", true);
		modelScoreMap.put("5_4", true);
		modelScoreMap.put("5_5", true);
		modelScoreMap.put("5_6", true);

		modelScoreMap.put("6_1", true);
		modelScoreMap.put("6_2", true);
		modelScoreMap.put("6_3", true);
		modelScoreMap.put("6_4", true);
		modelScoreMap.put("6_5", true);
		modelScoreMap.put("6_6", true);

		modelScoreMap.put("7_1", true);
		modelScoreMap.put("7_2", true);
		modelScoreMap.put("7_3", true);

		modelScoreMap.put("8_1", true);
		modelScoreMap.put("8_2", true);
		modelScoreMap.put("8_3", true);

		modelScoreMap.put("9_1", true);
		modelScoreMap.put("9_2", true);

		modelScoreMap.put("10_1", true);
		modelScoreMap.put("10_2", true);
	}

	public boolean validateState(String state) {
		if (StringUtils.isNotBlank(state))
			return stateMap.containsKey(state);
		else
			return false;
	}

	public boolean validateModelScore(int riskModelScore, int narModelScore) {
		String key = riskModelScore + "_" + narModelScore;
		return modelScoreMap.containsKey(key);
	}

	public boolean validateTerm(int term) {
		try {
			if (term == 60)
				return false;
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	public boolean validateLoan(String loan) {
		if (StringUtils.isNotBlank(loan) && loan.equalsIgnoreCase("Whole"))
			return false;
		else
			return true;
	}

	public boolean validateCoBorrowerApplication(boolean value) {
		if (!value)
			return false;
		else
			return true;
	}

	public boolean validateRequest(Result result) {
		if (result.getStated_monthly_income() < 1)
			return false;
		if (isBlank(result.getEmployment_status_description()))
			return false;
		if (result.getAmount_funded() < 1)
			return false;
		if (isBlank(result.getInvestment_type_description()))
			return false;
		if (result.getLender_yield() < 1)
			return false;
		if (result.getListing_monthly_payment() < 1)
			return false;
		// if(result.isIncome_verifiable())

		if (isBlank(result.getIncome_range_description()))
			return false;
		if (isBlank(result.getProsper_rating()))
			return false;
		if (result.getBorrower_rate() < 1)
			return false;
		if (result.getBorrower_apr() < 1)
			return false;
		// if(result.isPartial_funding_indicator())

		if (result.getLender_indicator() < 1)
			return false;
		if (result.getListing_category_id() < 1)
			return false;
		if (result.getListing_amount() < 1)
			return false;
		if (result.getPrior_prosper_loans() < 1)
			return false;
		if (result.getDti_wprosper_loan() < 1)
			return false;
		return true;
	}

	public boolean isBlank(String str) {
		if (str == null || str.isEmpty())
			return true;
		str = str.trim();
		if (str.length() > 0)
			return false;
		return true;
	}

}