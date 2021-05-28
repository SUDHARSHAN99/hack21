/**
 * 
 */
package com.example.adf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * @author sudharshanreddy
 *
 */
@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {
	
	public float amount_funded;
	public float amount_participation;
	public float amount_remaining;
	public boolean biddable;
	public float borrower_apr;
	public float borrower_rate;
	public String borrower_state;
	public String channel_code;
	public boolean co_borrower_application;

	//public CustomerBasicinfo customerBasicinfo;
	public CreditBureauValuesExperian credit_bureau_values_experian;
	public CreditBureauValuesTransunion credit_bureau_values_transunion;
	public CreditBureauBaluesTransunionTndexed credit_bureau_values_transunion_indexed;

	public String decision_bureau;
	public double dti_wprosper_loan;
	public String employment_status_description;
	public double estimated_monthly_housing_expense;
	public double funding_threshold;
	public boolean has_mortgage;
	public double historical_return;
	public double historical_return_10th_pctl;
	public double historical_return_90th_pctl;
	public int income_range;
	public String income_range_description;
	public boolean income_verifiable;
	public boolean invested;
	public int investment_product_id;
	public String investment_type_description;
	public int investment_typeid;
	public String last_updated_date;
	public int lender_indicator;
	public double lender_yield;
	public double listing_amount;
	public double listing_category_id;
	public String listing_creation_date;
	public int listing_duration;
	public double listing_monthly_payment;
	public long listing_number;
	public String listing_start_date;
	public int listing_status;
	public String listing_status_reason;
	public int listing_term;
	public String listing_title;
	public double max_prior_prosper_loan;
	public String member_key;
	public double min_prior_prosper_loan;
	public double months_employed;
	public String occupation;
	public boolean partial_funding_indicator;
	public double percent_funded;
	public int prior_prosper_loans;
	public int prior_prosper_loans_active;
	public double prior_prosper_loans_balance_outstanding;
	public int prior_prosper_loans_cycles_billed;
	public int prior_prosper_loans_late_cycles;
	public int prior_prosper_loans_late_payments_one_month_plus;
	public double prior_prosper_loans_ontime_payments;
	public int prior_prosper_loans_principal_borrowed;
	public double prior_prosper_loans_principal_outstanding;
	public String prosper_rating;
	public int prosper_score;
	public double stated_monthly_income;
	public int verification_stage;
	public String whole_loan_start_date;
	private String whole_loan;
}
