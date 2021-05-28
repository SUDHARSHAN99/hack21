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

/**
 * The persistent class for the bankaccount database table.
 * 
 */
@Entity
@Table(name = "leadBidStatus")
@NamedQuery(name = "leadBidStatus.findAll", query = "SELECT b FROM LeadBidStatus b")
public class LeadBidStatus implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@Column(name = "leadId")
	private long leadId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date datestamp;

	@Column(name = "narScore")
	private int narScore;

	@Column(name = "riskScore")
	private int riskScore;

	public LeadBidStatus() {
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

	public Date getDatestamp() {
		return datestamp;
	}

	public void setDatestamp(Date datestamp) {
		this.datestamp = datestamp;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "LeadBidStatus [id=" + id + ", leadId=" + leadId + ", datestamp=" + datestamp + ", narScore=" + narScore
				+ ", riskScore=" + riskScore + "]";
	}

}
