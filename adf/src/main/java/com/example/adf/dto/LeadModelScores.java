package com.example.adf.dto;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "leadBidStatus")
@NamedQuery(name = "leadBidStatus.findAll", query = "SELECT b FROM LeadBidStatus b")
public class LeadModelScores implements Serializable,Cloneable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private long id;

  @Column(name = "leadId")
  private long leadId;

   @Temporal(TemporalType.TIMESTAMP)
  private Date datestamp;

  @Column(name = "bidAmount")
  private int bidAmount;

  @Column(name = "listing_id")
  private int listing_id;
  

  @Column(name="bidStatus")
  private String bidStatus;


  public LeadModelScores() {}


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


public int getBidAmount() {
	return bidAmount;
}


public void setBidAmount(int bidAmount) {
	this.bidAmount = bidAmount;
}


public int getListing_id() {
	return listing_id;
}


public void setListing_id(int listing_id) {
	this.listing_id = listing_id;
}


public String getBidStatus() {
	return bidStatus;
}


public void setBidStatus(String bidStatus) {
	this.bidStatus = bidStatus;
}


@Override
public String toString() {
	return "LeadBidStatus [id=" + id + ", leadId=" + leadId + ", datestamp=" + datestamp + ", bidAmount=" + bidAmount
			+ ", listing_id=" + listing_id + ", bidStatus=" + bidStatus + "]";
}

}
