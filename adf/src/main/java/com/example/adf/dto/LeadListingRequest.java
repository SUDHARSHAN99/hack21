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
public class LeadListingRequest implements Serializable,Cloneable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private long id;

  @Column(name = "leadId")
  private long leadId;

   @Temporal(TemporalType.TIMESTAMP)
  private Date datestamp;

  @Override
public String toString() {
	return "LeadListingRequest [id=" + id + ", leadId=" + leadId + ", datestamp=" + datestamp + ", request=" + request
			+ "]";
}


@Column(name="request")
  private String request;


  public LeadListingRequest() {}


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


}
