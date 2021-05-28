package com.example.adf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.adf.api.GetListingApiDelegate;
import com.adf.hackathon.model.Listing;

@Service
public class ListingHandler implements GetListingApiDelegate {
	
	@Autowired
	HttpAgent httpAgent;
	
	public ResponseEntity<Listing> getListing() {
		
		return null;
	}
}
