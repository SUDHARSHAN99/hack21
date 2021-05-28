package com.example.adf.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.adf.api.GetListingApiDelegate;
import com.adf.hackathon.model.Listing;

@Service
public class ListingHandler implements GetListingApiDelegate {
	
	@Autowired
	HttpAgent httpAgent;
	@Value("${marketplace.listing.url}")
	private String marketListingUrl;
	@Override
	public ResponseEntity<Listing> getListing() {
		
		try {
			httpAgent.hitEndPointForGet(marketListingUrl);
		} catch (IOException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
