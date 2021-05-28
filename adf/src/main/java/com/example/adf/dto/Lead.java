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

/**
 * The persistent class for the bankaccount database table.
 * 
 */
@Entity
@Table(name = "lead")
@NamedQuery(name = "lead.findAll", query = "SELECT b FROM Lead b")
public class Lead implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@Column(name = "fname")
	private String fname;

	@Temporal(TemporalType.TIMESTAMP)
	private Date datestamp;

	@Column(name = "SFBankID")
	private String lname;

	@Column(name = "empType")
	private String empType;

	@Column(name = "empStatus")
	private Boolean empStatus;

	public Lead() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public Date getDatestamp() {
		return datestamp;
	}

	public void setDatestamp(Date datestamp) {
		this.datestamp = datestamp;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public Boolean getEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(Boolean empStatus) {
		this.empStatus = empStatus;
	}

	@Override
	public String toString() {
		return "Lead [id=" + id + ", fname=" + fname + ", datestamp=" + datestamp + ", lname=" + lname + ", empType="
				+ empType + ", empStatus=" + empStatus + "]";
	}

}
