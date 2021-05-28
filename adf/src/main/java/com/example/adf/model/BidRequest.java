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
public class BidRequest {
	public Long listing_id;
	public double bid_amount;
	public String bid_status;
	
	public BidRequest() {
		
	}
	
	public BidRequest(Long listing_id, double bid_amount) {
		super();
		this.listing_id = listing_id;
		this.bid_amount = bid_amount;
	}
	
}
