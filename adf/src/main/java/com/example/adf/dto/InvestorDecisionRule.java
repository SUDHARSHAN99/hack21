package com.example.adf.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "investorDecisionRule")
@NamedQuery(name = "investorDecisionRule.findAll", query = "SELECT b FROM InvestorDecisionRule b")
public class InvestorDecisionRule implements Serializable, Cloneable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@Column(name = "leadId")
	private long leadId;

	@Column(name = "created_dtm")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDtm;

	@Column(name = "rule_name")
	private String ruleName ;

	@Column(name = "rule_decision")
	private String ruleDecision;

	public InvestorDecisionRule() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLeadId() {
		return leadId;
	}

	public void setLeadId(long leadId) {
		this.leadId = leadId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleDecision() {
		return ruleDecision;
	}

	public void setRuleDecision(String ruleDecision) {
		this.ruleDecision = ruleDecision;
	}

	public Date getCreatedDtm() {
		return createdDtm;
	}

	public void setCreatedDtm(Date createdDtm) {
		this.createdDtm = createdDtm;
	}

	@Override
	public String toString() {
		return "InvestorDecisionRule [id=" + id + ", leadId=" + leadId + ", createdDtm=" + createdDtm + ", ruleName="
				+ ruleName + ", ruleDecision=" + ruleDecision + "]";
	}
	
}
