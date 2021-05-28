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
public class CreditBureauBaluesTransunionTndexed {
	public double at01s_credit_lines; 
	public double at02s_open_accounts;
	public double at03s_current_credit_lines;
	public double at20s_oldest_trade_open_date;
	public double at57s_amount_delinquent;
	public double bc34s_bankcard_utilization;
	public String credit_report_date; 
	public String fico_score; 
	public double g041s_accounts_30_or_more_days_past_due_ever; 
	public double g093s_number_of_public_records; 
	public double g094s_number_of_public_record_bankruptcies;
	public double g095s_months_since_most_recent_public_record;
	public double g099s_public_records_last_24_months; 
	public double g102s_months_since_most_recent_inquiry; 
	public double g218b_number_of_delinquent_accounts;
	public double g980s_inquiries_in_the_last_6_months; 
	public double re101s_revolving_balance;
	public double re20s_age_of_oldest_revolving_account_in_months; 
	public double re33s_balance_owed_on_all_revolving_accounts; 
	public double s207s_months_since_most_recent_public_record_bankruptcy; 
}
