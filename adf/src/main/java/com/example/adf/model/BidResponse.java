/**
 * 
 */
package com.example.adf.model;

import java.util.List;

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
public class BidResponse {
	List<BidRequest> bid_requests;
	public String order_date;
	public String order_id;
	public String order_status;
	public String source;
}
