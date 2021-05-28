package com.example.adf.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvesterControler {

	@Autowired
	HttpAgent httpAgent;
	@Value("${marketplace.listing.url}")
	private String marketListingUrl;
	private String hitEndPointForGet;

	@RequestMapping(value = "/prosper/list", method = RequestMethod.GET)
	public String testAPI(HttpServletRequest httpServletRequest,
			HttpServletResponse response) {

		try {
			hitEndPointForGet = httpAgent.hitEndPointForGet(marketListingUrl);
			System.out.println("hitEndPointForGet"+hitEndPointForGet);
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;

	}
}
