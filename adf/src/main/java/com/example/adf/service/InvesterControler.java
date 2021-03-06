package com.example.adf.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.adf.Helper.ControllerHelper;
import com.example.adf.dto.InvestorDecisionRule;
import com.example.adf.dto.LeadBidStatus;
import com.example.adf.dto.LeadModelScores;
import com.example.adf.dto.RepoHelper;
import com.example.adf.model.ResultList;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class InvesterControler {

	@Autowired
	HttpAgent httpAgent;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ControllerHelper ctrlHelper;
	@Value("${marketplace.listing.url}")
	private String marketListingUrl;
	
	@Autowired
	private ModelCallHandler modelCallHandler;
	
	@Autowired
	private RepoHelper repoHelper;
	
	@RequestMapping(value = "/prosper/list", method = RequestMethod.GET)
	public String investerList(HttpServletRequest httpServletRequest, HttpServletResponse response) {
		String hitEndPointForGet;
		try {
			System.out.println("marketListingUrl" + marketListingUrl);
			hitEndPointForGet = httpAgent.hitEndPointForGet(marketListingUrl);
			System.out.println("hitEndPointForGet" + hitEndPointForGet);
			ResultList ResultList = ctrlHelper.bindJsonToObj(hitEndPointForGet, ResultList.class);
			System.out.println("ResultList: received");
			modelCallHandler.processResultList(ResultList);
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String investerPing(HttpServletRequest httpServletRequest, HttpServletResponse response) {
		
		return "PoNg:)";

	}
	
	@RequestMapping(value = "/bidSatus", method = RequestMethod.GET)
	public String investerBidStatus(HttpServletRequest httpServletRequest, HttpServletResponse response) {
		List<LeadBidStatus> findAllLeadBidStatusList = repoHelper.findAllLeadBidStatusList();
		return findAllLeadBidStatusList.size()+"";
		
	}
	
	@RequestMapping(value = "/leadDecisionRule/{leadId}", method = RequestMethod.GET)
	public String investerLeadDecisionRuleStatus(@PathVariable String leadId) {
		List<InvestorDecisionRule> allInvestorDecisionRuleList = repoHelper.findByListingId(Long.parseLong(leadId));
		return allInvestorDecisionRuleList.toString();
		
	}
	
	@RequestMapping(value = "/getModelScores/{leadId}", method = RequestMethod.GET)
	public String getModelScores(@PathVariable String leadId) {
		LeadModelScores leadModelScore = repoHelper.findByListingIdOnModelScores(Long.parseLong(leadId));
		return leadModelScore.toString();
		
	}
}
